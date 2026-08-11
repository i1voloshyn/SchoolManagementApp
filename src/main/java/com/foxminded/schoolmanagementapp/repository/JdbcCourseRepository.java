package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCourseRepository implements CourseRepository {
    private static final String INSERT_COURSE_QUERY = """
            INSERT INTO courses (name, description)
            VALUES (:name, :description)
            """;
    private static final String DELETE_COURSE_QUERY = """
            DELETE FROM courses WHERE id = :id
            """;
    private static final String FIND_ALL_COURSES_QUERY = """
            SELECT id, name, description
            FROM courses
            """;
    private static final String FIND_COURSE_BY_ID_QUERY = """
            SELECT id, name, description
            FROM courses
            WHERE id = :id
            """;
    private static final String FIND_COURSES_BY_NAME_QUERY = """
            SELECT id, name, description
            FROM courses
            WHERE name = :name
            """;
    private static final String FIND_COURSES_BY_STUDENT_ID_QUERY = """
            SELECT c.id, c.name, c.description
            FROM courses c
            JOIN students_courses sc ON sc.course_id = c.id
            WHERE sc.student_id = :student_id
            """;
    private static final RowMapper<Course> COURSE_MAPPER = (resultSet, rowNumber) -> new Course(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("description")
    );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public JdbcCourseRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                JdbcTemplate jdbcTemplate,
                                TransactionTemplate transactionTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Course save(Course course) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", course.getName())
                .addValue("description", course.getDescription());

        return transactionTemplate.execute(status -> {
            int affectedRows = namedParameterJdbcTemplate.update(
                    INSERT_COURSE_QUERY,
                    parameters,
                    keyHolder,
                    new String[]{"id"}
            );
            validateQuery(INSERT_COURSE_QUERY, 1, affectedRows);

            course.setId(keyHolder.getKeyAs(Long.class));
            return course;
        });
    }

    @Override
    public void delete(Long id) {
        transactionTemplate.executeWithoutResult(status -> {
            int affectedRows = namedParameterJdbcTemplate.update(
                    DELETE_COURSE_QUERY,
                    new MapSqlParameterSource("id", id)
            );

            if (affectedRows == 0) {
                throw new CourseNotFoundException(id);
            }

            validateQuery(DELETE_COURSE_QUERY, 1, affectedRows);
        });
    }

    @Override
    public List<Course> findAll() {
        return jdbcTemplate.query(FIND_ALL_COURSES_QUERY, COURSE_MAPPER);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return namedParameterJdbcTemplate.query(
                        FIND_COURSE_BY_ID_QUERY,
                        new MapSqlParameterSource("id", id),
                        COURSE_MAPPER
                )
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Course> findByName(String name) {
        return namedParameterJdbcTemplate.query(
                        FIND_COURSES_BY_NAME_QUERY,
                        new MapSqlParameterSource("name", name),
                        COURSE_MAPPER
                ).stream()
                .findFirst();
    }

    @Override
    public List<Course> findByStudentId(Long studentId) {
        return namedParameterJdbcTemplate.query(
                FIND_COURSES_BY_STUDENT_ID_QUERY,
                new MapSqlParameterSource("student_id", studentId),
                COURSE_MAPPER
        );
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual);
        }
    }
}
