package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.EnrollmentException;
import com.foxminded.schoolmanagementapp.exception.EnrollmentNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcEnrollmentRepository.class)
@Testcontainers
class JdbcEnrollmentRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired
    EnrollmentRepository repository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void enroll_shouldCreateExpectedEnrollment() {
        Long studentId = findStudentId("Emily");
        Long courseId = findCourseId("SQL");

        repository.enroll(studentId, courseId);

        Long enrollmentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students_courses WHERE student_id = ? AND course_id = ?",
                Long.class,
                studentId,
                courseId
        );
        assertThat(enrollmentCount).isOne();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/insert_students_and_courses.sql"})
    @Test
    void enroll_shouldVerifyStudentAndCourse_andCreateExpectedEnrollment() {
        Long studentId = findStudentId("Emily");
        Long courseId = findCourseId("SQL");

        repository.enroll(studentId, courseId);

        Long enrollmentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students_courses WHERE student_id = ? AND course_id = ?",
                Long.class,
                studentId,
                courseId
        );
        assertThat(enrollmentCount).isOne();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/insert_students_and_courses.sql"})
    @Test
    void enroll_shouldVerifyStudentAndCourse_andDoNotCreateEnrollment_whenStudentIsMissing() {
        Long wrongStudentId = 99L;
        Long courseId = findCourseId("SQL");

        assertThatExceptionOfType(EnrollmentException.class)
                .isThrownBy(() -> repository.enroll(wrongStudentId, courseId));
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void enroll_shouldThrowException_whenEnrollmentAlreadyExists() {
        Long studentId = findStudentId("John");
        Long courseId = findCourseId("Java");

        assertThatException()
                .isThrownBy(() -> repository.enroll(studentId, courseId))
                .isInstanceOf(EnrollmentException.class);
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void enroll_shouldThrowException_whenStudentDoesNotExist() {
        Long courseId = findCourseId("Java");

        assertThatException()
                .isThrownBy(() -> repository.enroll(-1L, courseId))
                .isInstanceOf(EnrollmentException.class);
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void remove_shouldRemoveExpectedEnrollment() {
        Long studentId = findStudentId("John");
        Long courseId = findCourseId("SQL");

        repository.remove(studentId, courseId);

        Long enrollmentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students_courses WHERE student_id = ? AND course_id = ?",
                Long.class,
                studentId,
                courseId
        );
        assertThat(enrollmentCount).isZero();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void remove_shouldThrowException_whenEnrollmentDoesNotExist() {
        Long studentId = findStudentId("Emily");
        Long courseId = findCourseId("Java");

        assertThatException()
                .isThrownBy(() -> repository.remove(studentId, courseId))
                .isInstanceOf(EnrollmentNotFoundException.class);
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void exists_shouldReturnTrue_whenEnrollmentExists() {
        Long studentId = findStudentId("John");
        Long courseId = findCourseId("Java");

        boolean actual = repository.exists(studentId, courseId);

        assertThat(actual).isTrue();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void exists_shouldReturnFalse_whenEnrollmentDoesNotExist() {
        Long studentId = findStudentId("Emily");
        Long courseId = findCourseId("Java");

        boolean actual = repository.exists(studentId, courseId);

        assertThat(actual).isFalse();
    }

    private Long findStudentId(String firstName) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM students WHERE first_name = ?",
                Long.class,
                firstName
        );
    }

    private Long findCourseId(String courseName) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM courses WHERE name = ?",
                Long.class,
                courseName
        );
    }
}
