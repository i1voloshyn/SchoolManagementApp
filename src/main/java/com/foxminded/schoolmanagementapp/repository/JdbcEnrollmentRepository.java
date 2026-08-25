package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.EnrollmentException;
import com.foxminded.schoolmanagementapp.exception.EnrollmentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import java.sql.Statement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Repository
public class JdbcEnrollmentRepository implements EnrollmentRepository {
    private static final String ENROLL_STUDENT_QUERY =
            """
                    INSERT INTO students_courses (student_id, course_id)
                    VALUES (:student_id, :course_id)
                    """;
    private static final String REMOVE_STUDENT_QUERY =
            """
                    DELETE FROM students_courses
                    WHERE student_id = :student_id
                      AND course_id = :course_id
                    """;
    private static final String ENROLLMENT_EXISTS_QUERY =
            """
                    SELECT EXISTS (
                        SELECT 1
                        FROM students_courses
                        WHERE student_id = :student_id
                          AND course_id = :course_id
                    )
                    """;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public JdbcEnrollmentRepository(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            TransactionTemplate transactionTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void enroll(Long studentId, Long courseId) {
        try {
            transactionTemplate.executeWithoutResult(
                    status -> {
                        int affectedRows =
                                namedParameterJdbcTemplate.update(
                                        ENROLL_STUDENT_QUERY,
                                        enrollmentParameters(studentId, courseId));
                        validateQuery(ENROLL_STUDENT_QUERY, 1, affectedRows);
                    });
        } catch (DataIntegrityViolationException e) {
            log.warn("Enrollment rejected:studentId = {}, courseId = {}", studentId, courseId, e);

            throw new EnrollmentException(
                    "Cannot enroll student %d in course %d".formatted(studentId, courseId), e);
        }
    }

    @Override
    public void enrollAll(List<Enrollment> enrollments) {
        if (enrollments.isEmpty()) {
            return;
        }

        SqlParameterSource[] parameters =
                enrollments.stream()
                        .map(this::enrollmentParameters)
                        .toArray(SqlParameterSource[]::new);

        try {
            transactionTemplate.executeWithoutResult(
                    status -> {
                        int[] affectedRows =
                                namedParameterJdbcTemplate.batchUpdate(
                                        ENROLL_STUDENT_QUERY, parameters);
                        validateBatch(affectedRows, enrollments.size());
                    });
        } catch (DataIntegrityViolationException e) {
            log.warn("Batch enrollment rejected", e);

            throw new EnrollmentException("Cannot create enrollment batch", e);
        }
    }

    @Override
    public void remove(Long studentId, Long courseId) {
        transactionTemplate.executeWithoutResult(
                status -> {
                    int affectedRows =
                            namedParameterJdbcTemplate.update(
                                    REMOVE_STUDENT_QUERY,
                                    enrollmentParameters(studentId, courseId));
                    if (affectedRows == 0) {
                        throw new EnrollmentNotFoundException(studentId, courseId);
                    }
                    validateQuery(REMOVE_STUDENT_QUERY, 1, affectedRows);
                });
    }

    @Override
    public boolean exists(Long studentId, Long courseId) {
        Boolean exists =
                namedParameterJdbcTemplate.queryForObject(
                        ENROLLMENT_EXISTS_QUERY,
                        enrollmentParameters(studentId, courseId),
                        Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    private MapSqlParameterSource enrollmentParameters(Long studentId, Long courseId) {
        return new MapSqlParameterSource()
                .addValue("student_id", studentId)
                .addValue("course_id", courseId);
    }

    private MapSqlParameterSource enrollmentParameters(Enrollment enrollment) {
        return enrollmentParameters(enrollment.studentId(), enrollment.courseId());
    }

    private void validateBatch(int[] affectedRows, int expectedRows) {
        if (affectedRows.length != expectedRows) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
                    ENROLL_STUDENT_QUERY, expectedRows, affectedRows.length);
        }

        for (int affectedRow : affectedRows) {
            if (affectedRow != 1 && affectedRow != Statement.SUCCESS_NO_INFO) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
                        ENROLL_STUDENT_QUERY, 1, affectedRow);
            }
        }
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual);
        }
    }
}
