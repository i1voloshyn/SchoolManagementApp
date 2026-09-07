package com.foxminded.schoolmanagementapp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcCourseRepository.class)
@Testcontainers
class JdbcCourseRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired
    CourseRepository repository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Sql("/fixtures/clean_up.sql")
    @Test
    void save_shouldSaveAndReturnCourse_withGeneratedId() {
        Course courseToSave = Course.builder()
                .id(null)
                .name("Test Course")
                .description("Test course description")
                .students(Set.of())
                .build();

        Course saved = repository.save(courseToSave);

        Course actual =
                jdbcTemplate.queryForObject(
                        "SELECT id, name, description FROM courses WHERE id = ?",
                        (resultSet, rowNumber) ->
                                Course.builder()
                                        .id(resultSet.getLong("id"))
                                        .name(resultSet.getString("name"))
                                        .description(resultSet.getString("description"))
                                        .students(Set.of())
                                        .build(),
                        saved.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(actual).isEqualTo(saved);
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/courses/insert_five_courses.sql"})
    @Test
    void delete_shouldDeleteExpectedCourse() {
        Long courseIdToDelete =
                jdbcTemplate.queryForObject("SELECT id FROM courses LIMIT 1", Long.class);

        repository.delete(courseIdToDelete);

        Long remainingCourses =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM courses WHERE id = ?", Long.class, courseIdToDelete);

        assertThat(remainingCourses).isZero();
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void delete_shouldThrowException_whenCourseDoesNotExist() {
        assertThatException()
                .isThrownBy(() -> repository.delete(-1L))
                .isInstanceOf(CourseNotFoundException.class);
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
                jdbcTemplate.queryForObject(
                        "SELECT id FROM courses WHERE name = ?", Long.class, "Spring");

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
                jdbcTemplate.queryForObject(
                        "SELECT id FROM students WHERE first_name = ?", Long.class, "John");

        List<Course> actual = repository.findByStudentId(studentId);

        assertThat(actual).extracting(Course::getName).containsExactlyInAnyOrder("Java", "SQL");
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void findByStudentId_shouldReturnEmptyList_whenStudentHasNoEnrollments() {
        Long studentId =
                jdbcTemplate.queryForObject(
                        "SELECT id FROM students WHERE first_name = ?", Long.class, "Emily");

        List<Course> actual = repository.findByStudentId(studentId);

        assertThat(actual).isEmpty();
    }
}
