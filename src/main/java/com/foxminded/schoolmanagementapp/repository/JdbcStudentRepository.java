package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Student;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcStudentRepository implements StudentsRepository {
    private static final String INSERT_STUDENT_QUERY = """
            INSERT INTO students (group_id, first_name, last_name)
            VALUES (:group_id, :first_name, :last_name)
            """;
    private static final String DELETE_STUDENT_QUERY = """
            DELETE FROM students WHERE id = :id
            """;
    private static final String FIND_ALL_STUDENTS_QUERY = """
            SELECT id, group_id, first_name, last_name
            FROM students
            """;
    private static final String FIND_STUDENT_BY_ID_QUERY = """
            SELECT id, group_id, first_name, last_name
            FROM students
            WHERE id = :id
            """;
    private static final String FIND_STUDENTS_BY_LAST_NAME_QUERY = """
            SELECT id, group_id, first_name, last_name
            FROM students
            WHERE last_name = :last_name
            """;
    private static final String FIND_STUDENTS_BY_COURSE_ID_QUERY = """
            SELECT s.id, s.group_id, s.first_name, s.last_name
            FROM students s
            JOIN students_courses sc ON sc.student_id = s.id
            WHERE sc.course_id = :id
            """;
    private static final RowMapper<@Nullable Student> STUDENT_MAPPER = (rs, rowNumber) -> {
        Long groupId = rs.getLong("group_id");
        return new Student(
                rs.getLong("id"),
                groupId,
                rs.getString("first_name"),
                rs.getString("last_name")
        );
    };

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public JdbcStudentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                 JdbcTemplate jdbcTemplate,
                                 TransactionTemplate transactionTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Student save(Student student) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("group_id", student.getGroupId())
                .addValue("first_name", student.getFirstName())
                .addValue("last_name", student.getLastName());

        return transactionTemplate.execute(status -> {
            int affectedRows = namedParameterJdbcTemplate.update(
                    INSERT_STUDENT_QUERY,
                    parameters,
                    keyHolder,
                    new String[]{"id"}
            );
            validateQuery(INSERT_STUDENT_QUERY, 1, affectedRows);

            student.setId(keyHolder.getKeyAs(Long.class));
            return student;
        });
    }

    @Override
    public List<Student> saveAll(List<Student> students) {
        if (students.isEmpty()) {
            return List.of();
        }

        MapSqlParameterSource[] parameters = students.stream()
                .map(this::studentParameters)
                .toArray(MapSqlParameterSource[]::new);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        return transactionTemplate.execute(status -> {
            int[] affectedRows = namedParameterJdbcTemplate.batchUpdate(
                    INSERT_STUDENT_QUERY,
                    parameters,
                    keyHolder,
                    new String[]{"id"}
            );
            validateBatch(affectedRows, students.size());
            assignGeneratedIds(students, keyHolder.getKeyList());
            return List.copyOf(students);
        });
    }

    @Override
    public void delete(Long id) {
        transactionTemplate.executeWithoutResult(status -> {
            int affectedRows = namedParameterJdbcTemplate.update(
                    DELETE_STUDENT_QUERY,
                    new MapSqlParameterSource("id", id)
            );
            validateQuery(DELETE_STUDENT_QUERY, 1, affectedRows);
        });
    }

    @Override
    public List<Student> findAll() {
        return jdbcTemplate.query(FIND_ALL_STUDENTS_QUERY, STUDENT_MAPPER);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return namedParameterJdbcTemplate.query(
                        FIND_STUDENT_BY_ID_QUERY,
                        new MapSqlParameterSource("id", id),
                        STUDENT_MAPPER
                )
                .stream()
                .findFirst();
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        return namedParameterJdbcTemplate.query(
                FIND_STUDENTS_BY_LAST_NAME_QUERY,
                new MapSqlParameterSource("last_name", lastName),
                STUDENT_MAPPER
        );
    }

    @Override
    public List<Student> findByCourseId(Long id) {
        return namedParameterJdbcTemplate.query(
                FIND_STUDENTS_BY_COURSE_ID_QUERY,
                new MapSqlParameterSource("id", id),
                STUDENT_MAPPER
        );
    }

    private MapSqlParameterSource studentParameters(Student student) {
        return new MapSqlParameterSource()
                .addValue("group_id", student.getGroupId())
                .addValue("first_name", student.getFirstName())
                .addValue("last_name", student.getLastName());
    }

    private void assignGeneratedIds(List<Student> students, List<Map<String, Object>> generatedKeys) {
        if (generatedKeys.size() != students.size()) {
            throw new IllegalStateException(
                    "Expected %d generated student IDs but received %d"
                            .formatted(students.size(), generatedKeys.size())
            );
        }

        for (int index = 0; index < students.size(); index++) {
            Object generatedId = generatedKeys.get(index).get("id");
            if (!(generatedId instanceof Number number)) {
                throw new IllegalStateException("Student ID was not generated for batch row " + index);
            }
            students.get(index).setId(number.longValue());
        }
    }

    private void validateBatch(int[] affectedRows, int expectedRows) {
        if (affectedRows.length != expectedRows) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
                    INSERT_STUDENT_QUERY,
                    expectedRows,
                    affectedRows.length
            );
        }

        for (int affectedRow : affectedRows) {
            if (affectedRow != 1 && affectedRow != Statement.SUCCESS_NO_INFO) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
                        INSERT_STUDENT_QUERY,
                        1,
                        affectedRow
                );
            }
        }
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual);
        }
    }
}
