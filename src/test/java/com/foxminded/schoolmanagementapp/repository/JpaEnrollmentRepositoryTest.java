package com.foxminded.schoolmanagementapp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.foxminded.schoolmanagementapp.exception.EnrollmentException;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
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
@Import(JpaEnrollmentRepository.class)
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaEnrollmentRepositoryTest {

    @Container @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired EnrollmentRepository repository;
    @Autowired EntityManagerFactory emf;

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void enroll_shouldCreateExpectedEnrollment() {
        Long studentId = findStudentId("Emily");
        Long courseId = findCourseId("SQL");

        repository.enroll(studentId, courseId);

        Long enrollmentCount = countEnrollment(studentId, courseId);
        assertThat(enrollmentCount).isOne();
    }

    @Sql(
            value = {
                "/fixtures/clean_up.sql",
                "/fixtures/enrollments/insert_students_and_courses.sql"
            })
    @Test
    void enrollAll_shouldCreateAllEnrollmentsInBatch() {
        Long johnId = findStudentId("John");
        Long annaId = findStudentId("Anna");
        Long javaId = findCourseId("Java");
        Long sqlId = findCourseId("SQL");
        List<Enrollment> enrollments =
                List.of(
                        new Enrollment(johnId, javaId),
                        new Enrollment(johnId, sqlId),
                        new Enrollment(annaId, javaId));

        repository.enrollAll(enrollments);

        Long enrollmentCount = countEnrollments();
        assertThat(enrollmentCount).isEqualTo(3L);
    }

    @Sql(
            value = {
                "/fixtures/clean_up.sql",
                "/fixtures/enrollments/insert_students_and_courses.sql"
            })
    @Test
    void enroll_shouldVerifyStudentAndCourse_andCreateExpectedEnrollment() {
        Long studentId = findStudentId("Emily");
        Long courseId = findCourseId("SQL");

        repository.enroll(studentId, courseId);

        Long enrollmentCount = countEnrollment(studentId, courseId);
        assertThat(enrollmentCount).isOne();
    }

    @Sql(
            value = {
                "/fixtures/clean_up.sql",
                "/fixtures/enrollments/insert_students_and_courses.sql"
            })
    @Test
    void enroll_shouldVerifyStudentAndCourse_andDoNotCreateEnrollment_whenStudentIsMissing() {
        Long wrongStudentId = 99L;
        Long courseId = findCourseId("SQL");

        assertThatExceptionOfType(EnrollmentException.class)
                .isThrownBy(() -> repository.enroll(wrongStudentId, courseId));
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void enroll_shouldThrowException_whenEnrollmentAlreadyExists() {
        Long studentId = findStudentId("John");
        Long courseId = findCourseId("Java");

        assertThatException()
                .isThrownBy(() -> repository.enroll(studentId, courseId))
                .isInstanceOf(EnrollmentException.class);
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void enroll_shouldThrowException_whenStudentDoesNotExist() {
        Long courseId = findCourseId("Java");

        assertThatException()
                .isThrownBy(() -> repository.enroll(-1L, courseId))
                .isInstanceOf(EnrollmentException.class);
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void remove_shouldRemoveExpectedEnrollment() {
        Long studentId = findStudentId("John");
        Long courseId = findCourseId("SQL");

        repository.remove(studentId, courseId);

        Long enrollmentCount = countEnrollment(studentId, courseId);
        assertThat(enrollmentCount).isZero();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void exists_shouldReturnTrue_whenEnrollmentExists() {
        Long studentId = findStudentId("John");
        Long courseId = findCourseId("Java");

        boolean actual = repository.exists(studentId, courseId);

        assertThat(actual).isTrue();
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void exists_shouldReturnFalse_whenEnrollmentDoesNotExist() {
        Long studentId = findStudentId("Emily");
        Long courseId = findCourseId("Java");

        boolean actual = repository.exists(studentId, courseId);

        assertThat(actual).isFalse();
    }

    private Long findStudentId(String firstName) {
        return emf.callInTransaction(
                em ->
                        em.createQuery(
                                        "SELECT s.id FROM Student s WHERE s.firstName = :firstName",
                                        Long.class)
                                .setParameter("firstName", firstName)
                                .getSingleResult());
    }

    private Long findCourseId(String courseName) {
        return emf.callInTransaction(
                em ->
                        em.createQuery(
                                        "SELECT c.id FROM Course c WHERE c.name = :courseName",
                                        Long.class)
                                .setParameter("courseName", courseName)
                                .getSingleResult());
    }

    private Long countEnrollment(Long studentId, Long courseId) {
        return emf.callInTransaction(
                em ->
                        ((Number)
                                        em.createNativeQuery(
                                                        """
                                                        SELECT COUNT(*)
                                                        FROM students_courses
                                                        WHERE student_id = :studentId
                                                          AND course_id = :courseId
                                                        """)
                                                .setParameter("studentId", studentId)
                                                .setParameter("courseId", courseId)
                                                .getSingleResult())
                                .longValue());
    }

    private Long countEnrollments() {
        return emf.callInTransaction(
                em ->
                        ((Number)
                                        em.createNativeQuery(
                                                        """
                                                        SELECT COUNT(*)
                                                        FROM students_courses
                                                        """)
                                                .getSingleResult())
                                .longValue());
    }
}
