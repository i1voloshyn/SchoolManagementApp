package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Course;
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
public class JdbcCourseRepository implements CourseRepository {
    private static final RowMapper<Course> COURSE_MAPPER = (resultSet, rowNumber) -> new Course(
            resultSet.getLong("course_id"),
            resultSet.getString("course_name"),
            resultSet.getString("course_description")
    );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public JdbcCourseRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Course save(Course course) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("course_name", course.getName())
                .addValue("course_description", course.getDescription());

        int affectedRows = namedParameterJdbcTemplate.update(
                getInsertCourseQuery(),
                parameters,
                keyHolder,
                new String[]{"course_id"}
        );
        validateQuery(getInsertCourseQuery(), 1, affectedRows);


        course.setId(keyHolder.getKeyAs(Long.class));
        return course;
    }

    @Override
    public void delete(Long id) {
        int affectedRows = namedParameterJdbcTemplate.update(
                getDeleteCourseQuery(),
                new MapSqlParameterSource("course_id", id)
        );
        validateQuery(getDeleteCourseQuery(), 1, affectedRows);
    }

    @Override
    public List<Course> findAll() {
        return jdbcTemplate.query(getFindAllCoursesQuery(), COURSE_MAPPER);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return namedParameterJdbcTemplate.query(
                        getFindCourseByIdQuery(),
                        new MapSqlParameterSource("course_id", id),
                        COURSE_MAPPER
                )
                .stream()
                .findFirst();
    }

    @Override
    public List<Course> findByName(String name) {
        return namedParameterJdbcTemplate.query(
                getFindCoursesByNameQuery(),
                new MapSqlParameterSource("course_name", name),
                COURSE_MAPPER
        );
    }

    private String getInsertCourseQuery() {
        return """
                INSERT INTO courses (course_name, course_description)
                VALUES (:course_name, :course_description)
                """;
    }

    private String getDeleteCourseQuery() {
        return """
                DELETE FROM courses WHERE course_id = :course_id
                """;
    }

    private String getFindAllCoursesQuery() {
        return """
                SELECT course_id, course_name, course_description
                FROM courses
                """;
    }

    private String getFindCourseByIdQuery() {
        return """
                SELECT course_id, course_name, course_description
                FROM courses
                WHERE course_id = :course_id
                """;
    }

    private String getFindCoursesByNameQuery() {
        return """
                SELECT course_id, course_name, course_description
                FROM courses
                WHERE course_name = :course_name
                """;
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual);
        }
    }
}
