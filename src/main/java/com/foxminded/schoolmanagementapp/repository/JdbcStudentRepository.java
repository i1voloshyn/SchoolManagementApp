package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Student;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcStudentRepository implements StudentsRepository {
    private static final RowMapper<Student> STUDENT_MAPPER = (rs, rowNumber) -> {
        Long groupId = rs.getLong("group_id");
        return new Student(
                rs.getLong("student_id"),
                groupId,
                rs.getString("first_name"),
                rs.getString("last_name")
        );
    };

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public JdbcStudentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                 JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Student save(Student student) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("group_id", student.getGroupId())
                .addValue("first_name", student.getFirstName())
                .addValue("last_name", student.getLastName());

        int affectedRows = namedParameterJdbcTemplate.update(
                getInsertStudentQuery(),
                parameters,
                keyHolder,
                new String[]{"student_id"}
        );
        validateQuery(getInsertStudentQuery(), 1, affectedRows);

        student.setId(keyHolder.getKeyAs(Long.class));
        return student;
    }

    @Override
    public void delete(Long id) {
        int affectedRows = namedParameterJdbcTemplate.update(
                getDeleteStudentQuery(),
                new MapSqlParameterSource("student_id", id)
        );
        validateQuery(getDeleteStudentQuery(), 1, affectedRows);
    }

    @Override
    public List<Student> findAll() {
        return jdbcTemplate.query(getFindAllStudentsQuery(), STUDENT_MAPPER);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return namedParameterJdbcTemplate.query(
                        getFindStudentByIdQuery(),
                        new MapSqlParameterSource("student_id", id),
                        STUDENT_MAPPER
                )
                .stream()
                .findFirst();
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        return namedParameterJdbcTemplate.query(
                getFindStudentsByLastNameQuery(),
                new MapSqlParameterSource("last_name", lastName),
                STUDENT_MAPPER
        );
    }

    @Override
    public List<Student> findByCourseId(Long courseId) {
        return namedParameterJdbcTemplate.query(
                getFindStudentsByCourseIdQuery(),
                new MapSqlParameterSource("course_id", courseId),
                STUDENT_MAPPER
        );
    }

    private String getInsertStudentQuery() {
        return """
                INSERT INTO students (group_id, first_name, last_name)
                VALUES (:group_id, :first_name, :last_name)
                """;
    }

    private String getDeleteStudentQuery() {
        return """
                DELETE FROM students WHERE student_id = :student_id
                """;
    }

    private String getFindAllStudentsQuery() {
        return """
                SELECT student_id, group_id, first_name, last_name
                FROM students
                """;
    }

    private String getFindStudentByIdQuery() {
        return """
                SELECT student_id, group_id, first_name, last_name
                FROM students
                WHERE student_id = :student_id
                """;
    }

    private String getFindStudentsByLastNameQuery() {
        return """
                SELECT student_id, group_id, first_name, last_name
                FROM students
                WHERE last_name = :last_name
                """;
    }

    private String getFindStudentsByCourseIdQuery() {
        return """
                SELECT s.student_id, s.group_id, s.first_name, s.last_name
                FROM students s
                JOIN students_courses sc ON sc.student_id = s.student_id
                WHERE sc.course_id = :course_id
                """;
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual);
        }
    }
}
