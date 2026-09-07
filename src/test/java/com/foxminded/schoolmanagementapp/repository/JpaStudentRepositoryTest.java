package com.foxminded.schoolmanagementapp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.model.Student;

import jakarta.persistence.EntityManagerFactory;

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

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaStudentRepository.class)
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaStudentRepositoryTest {

    @Container @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired StudentsRepository repository;
    @Autowired EntityManagerFactory emf;

    @Sql("/fixtures/clean_up.sql")
    @Test
    void save_shouldSaveAndReturnStudent_withGeneratedId() {
        Group group =
                emf.callInTransaction(
                        em -> {
                            Group groupToSave = Group.builder().name("Test Group").build();
                            em.persist(groupToSave);
                            return groupToSave;
                        });

        Student studentToSave = student(null, "Name", "Last", group);

        Student saved = repository.save(studentToSave);

        Student actual = emf.callInTransaction(em -> em.find(Student.class, saved.getId()));

        assertThat(saved.getId()).isNotNull();
        assertThat(actual)
                .extracting(Student::getFirstName, Student::getLastName)
                .containsExactly(studentToSave.getFirstName(), studentToSave.getLastName());
        assertThat(actual.getGroup().getId()).isEqualTo(group.getId());
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void saveAll_shouldSaveBatchAndReturnStudents_withGeneratedIds() {
        List<Student> students =
                List.of(
                        student(null, "John", "Doe", null),
                        student(null, "Jane", "Smith", null),
                        student(null, "Alice", "Johnson", null));

        List<Student> saved = repository.saveAll(students);

        assertThat(saved)
                .hasSize(3)
                .allSatisfy(student -> assertThat(student.getId()).isPositive());
        assertThat(saved).extracting(Student::getId).doesNotHaveDuplicates();

        Long persistedStudents =
                emf.callInTransaction(
                        em ->
                                em.createQuery("SELECT COUNT(s) FROM Student s", Long.class)
                                        .getSingleResult());
        assertThat(persistedStudents).isEqualTo(3L);
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/students/insert_five_students.sql"})
    @Test
    void delete_shouldDeleteExpectedStudent() {
        Long studentIdToDelete =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT s.id FROM Student s WHERE s.firstName ="
                                                    + " :firstName",
                                                Long.class)
                                        .setParameter("firstName", "John")
                                        .getSingleResult());

        repository.delete(studentIdToDelete);

        Long remainingStudents =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT COUNT(s) FROM Student s WHERE s.id = :id",
                                                Long.class)
                                        .setParameter("id", studentIdToDelete)
                                        .getSingleResult());

        assertThat(remainingStudents).isZero();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/students/insert_five_students.sql"})
    @Test
    void delete_shouldThrowException_forNonExistingStudentId() {
        Long nonExistingId = 999L;
        assertThatExceptionOfType(StudentNotFoundException.class)
                .isThrownBy(() -> repository.delete(nonExistingId));
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void delete_shouldDeleteStudentEnrollments_butPreserveCourses() {
        Long coursesBeforeDelete = 3L;
        Long studentIdToDelete =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT s.id FROM Student s WHERE s.firstName ="
                                                    + " :firstName",
                                                Long.class)
                                        .setParameter("firstName", "John")
                                        .getSingleResult());
        Long enrollmentsBeforeDelete = countStudentEnrollments(studentIdToDelete);

        repository.delete(studentIdToDelete);

        Long remainingStudents =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT COUNT(s) FROM Student s WHERE s.id = :id",
                                                Long.class)
                                        .setParameter("id", studentIdToDelete)
                                        .getSingleResult());
        Long remainingEnrollments = countStudentEnrollments(studentIdToDelete);
        Long remainingCourses =
                emf.callInTransaction(
                        em ->
                                em.createQuery("SELECT COUNT(c) FROM Course c", Long.class)
                                        .getSingleResult());

        assertThat(enrollmentsBeforeDelete).isEqualTo(2L);
        assertThat(remainingStudents).isZero();
        assertThat(remainingEnrollments).isZero();
        assertThat(remainingCourses).isEqualTo(coursesBeforeDelete);
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/students/insert_five_students.sql"})
    @Test
    void findAll_shouldReturnAllStudents_fromDatabase() {
        List<String> expectedFirstNames = List.of("John", "Anna", "Mark", "Kate", "Emily");

        List<Student> actual = repository.findAll();

        assertThat(actual)
                .extracting(Student::getFirstName)
                .containsExactlyInAnyOrderElementsOf(expectedFirstNames);
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseIsEmpty() {
        List<Student> actual = repository.findAll();

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/students/insert_five_students.sql"})
    @Test
    void findById_shouldReturnExpectedStudent_whenStudentExists() {
        Long studentId =
                emf.callInTransaction(
                        em ->
                                em.createQuery(
                                                "SELECT s.id FROM Student s WHERE s.firstName ="
                                                    + " :firstName",
                                                Long.class)
                                        .setParameter("firstName", "Mark")
                                        .getSingleResult());

        Optional<Student> actual = repository.findById(studentId);

        assertThat(actual)
                .isPresent()
                .get()
                .extracting(Student::getFirstName, Student::getLastName)
                .containsExactly("Mark", "Brown");
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void findById_shouldReturnEmptyOptional_whenStudentDoesNotExist() {
        Optional<Student> actual = repository.findById(-1L);

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/students/insert_five_students.sql"})
    @Test
    void findByLastName_shouldReturnAllStudents_withExpectedLastName() {
        List<Student> actual = repository.findByLastName("Smith");

        assertThat(actual)
                .extracting(Student::getFirstName)
                .containsExactlyInAnyOrder("John", "Anna");
        assertThat(actual).extracting(Student::getLastName).containsOnly("Smith");
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/students/insert_five_students.sql"})
    @Test
    void findByLastName_shouldReturnEmptyList_whenLastNameDoesNotExist() {
        List<Student> actual = repository.findByLastName("Unknown");

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void findByCourseName_shouldReturnAllStudents_enrolledInExpectedCourse() {
        String name = "Java";
        List<Student> actual = repository.findByCourseName(name);

        assertThat(actual)
                .extracting(Student::getFirstName)
                .containsExactlyInAnyOrder("John", "Anna");
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void findByCourseName_shouldReturnEmptyList_forNonExistedCourse() {
        String name = "Wrong-Java";
        List<Student> actual = repository.findByCourseName(name);

        assertThat(actual).isEmpty();
    }

    private Long countStudentEnrollments(Long studentId) {
        return emf.callInTransaction(
                em ->
                        ((Number)
                                        em.createNativeQuery(
                                                        """
                                                        SELECT COUNT(*)
                                                        FROM students_courses
                                                        WHERE student_id = :studentId
                                                        """)
                                                .setParameter("studentId", studentId)
                                                .getSingleResult())
                                .longValue());
    }

    private Student student(Long id, String firstName, String lastName, Group group) {
        return Student.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .group(group)
                .build();
    }
}
