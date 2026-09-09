package com.foxminded.schoolmanagementapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.foxminded.schoolmanagementapp.model.Course;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaCourseRepository.class)
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaCourseRepositoryTest {

    @Container @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired CourseRepository repository;
    @Autowired EntityManagerFactory emf;

    @Sql("/fixtures/clean_up.sql")
    @Test
    void save_shouldSaveAndReturnCourse_withGeneratedId() {
        Course courseToSave =
                Course.builder()
                        .id(null)
                        .name("Test Course")
                        .description("Test course description")
                        .students(Set.of())
                        .build();

        Course saved = repository.save(courseToSave);

        Course actual = emf.callInTransaction(em -> em.find(Course.class, saved.getId()));

        assertThat(saved.getId()).isNotNull();
        assertThat(actual)
                .extracting(Course::getName, Course::getDescription)
                .containsExactly(courseToSave.getName(), courseToSave.getDescription());
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/courses/insert_five_courses.sql"})
    @Test
    void update_shouldUpdateAndReturnCourse() {
        Long courseId =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT c.id FROM Course c WHERE c.name = :name",
                                                Long.class)
                                        .setParameter("name", "Java")
                                        .getSingleResult());
        Course courseToUpdate =
                Course.builder()
                        .id(courseId)
                        .name("Updated Java")
                        .description("Updated Java course description")
                        .build();

        Course updated = repository.update(courseToUpdate);

        Course persisted = emf.callInTransaction(em -> em.find(Course.class, courseId));
        assertThat(updated)
                .extracting(Course::getId, Course::getName, Course::getDescription)
                .containsExactly(
                        courseId, "Updated Java", "Updated Java course description");
        assertThat(persisted)
                .extracting(Course::getId, Course::getName, Course::getDescription)
                .containsExactly(
                        courseId, "Updated Java", "Updated Java course description");
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/courses/insert_five_courses.sql"})
    @Test
    void delete_shouldDeleteExpectedCourse() {
        Long courseIdToDelete =
                emf.callInTransaction(
                        em ->
                                em.createQuery("SELECT c.id FROM Course c", Long.class)
                                        .setMaxResults(1)
                                        .getSingleResult());

        repository.delete(courseIdToDelete);

        Long remainingCourses =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT COUNT(c) FROM Course c WHERE c.id = :id",
                                                Long.class)
                                        .setParameter("id", courseIdToDelete)
                                        .getSingleResult());

        assertThat(remainingCourses).isZero();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/courses/insert_five_courses.sql"})
    @Test
    void findAll_shouldReturnAllCourses_fromDatabase() {
        List<String> expectedDescriptions =
                List.of(
                        "Java fundamentals",
                        "Advanced Java Course",
                        "Relational databases and SQL",
                        "Spring Framework fundamentals",
                        "Version control with Git");

        List<Course> actual = repository.findAll();

        assertThat(actual)
                .extracting(Course::getDescription)
                .containsExactlyInAnyOrderElementsOf(expectedDescriptions);
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseIsEmpty() {
        List<Course> actual = repository.findAll();

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/courses/insert_five_courses.sql"})
    @Test
    void findById_shouldReturnExpectedCourse_whenCourseExists() {
        Long courseId =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT c.id FROM Course c WHERE c.name = :name",
                                                Long.class)
                                        .setParameter("name", "Spring")
                                        .getSingleResult());

        Optional<Course> actual = repository.findById(courseId);

        assertThat(actual)
                .isPresent()
                .get()
                .extracting(Course::getName, Course::getDescription)
                .containsExactly("Spring", "Spring Framework fundamentals");
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void findById_shouldReturnEmptyOptional_whenCourseDoesNotExist() {
        Optional<Course> actual = repository.findById(-1L);

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/courses/insert_five_courses.sql"})
    @Test
    void findByName_shouldReturnOneCourse_withExpectedName() {
        Optional<Course> actual = repository.findByName("Java");

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).extracting(Course::getName).isEqualTo("Java");
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/courses/insert_five_courses.sql"})
    @Test
    void findByName_shouldReturnEmptyOptional_whenNameDoesNotExist() {
        Optional<Course> actual = repository.findByName("Unknown");

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void findByStudentId_shouldReturnAllCourses_forExpectedStudent() {
        Long studentId =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT s.id FROM Student s WHERE s.firstName ="
                                                    + " :firstName",
                                                Long.class)
                                        .setParameter("firstName", "John")
                                        .getSingleResult());

        List<Course> actual = repository.findByStudentId(studentId);

        assertThat(actual).extracting(Course::getName).containsExactlyInAnyOrder("Java", "SQL");
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void findByStudentId_shouldReturnEmptyList_whenStudentHasNoEnrollments() {
        Long studentId =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT s.id FROM Student s WHERE s.firstName ="
                                                    + " :firstName",
                                                Long.class)
                                        .setParameter("firstName", "Emily")
                                        .getSingleResult());

        List<Course> actual = repository.findByStudentId(studentId);

        assertThat(actual).isEmpty();
    }
}
