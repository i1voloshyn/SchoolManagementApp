package com.foxminded.schoolmanagementapp.repository;

import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEnrollmentRepository implements EnrollmentRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcEnrollmentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void enroll(Long studentId, Long courseId) {
        int affectedRows = namedParameterJdbcTemplate.update(
                getEnrollStudentQuery(),
                enrollmentParameters(studentId, courseId)
        );
        validateQuery(getEnrollStudentQuery(), 1, affectedRows);
    }

    @Override
    public void remove(Long studentId, Long courseId) {
        int affectedRows = namedParameterJdbcTemplate.update(
                getRemoveStudentQuery(),
                enrollmentParameters(studentId, courseId)
        );
        validateQuery(getRemoveStudentQuery(), 1, affectedRows);
    }

    @Override
    public boolean exists(Long studentId, Long courseId) {
        Boolean exists = namedParameterJdbcTemplate.queryForObject(
                getEnrollmentExistsQuery(),
                enrollmentParameters(studentId, courseId),
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }

    private MapSqlParameterSource enrollmentParameters(Long studentId, Long courseId) {
        return new MapSqlParameterSource()
                .addValue("student_id", studentId)
                .addValue("course_id", courseId);
    }

    private String getEnrollStudentQuery() {
        return """
                INSERT INTO students_courses (student_id, course_id)
                VALUES (:student_id, :course_id)
                """;
    }

    private String getRemoveStudentQuery() {
        return """
                DELETE FROM students_courses
                WHERE student_id = :student_id
                  AND course_id = :course_id
                """;
    }

    private String getEnrollmentExistsQuery() {
        return """
                SELECT EXISTS (
                    SELECT 1
                    FROM students_courses
                    WHERE student_id = :student_id
                      AND course_id = :course_id
                )
                """;
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual);
        }
    }
}
