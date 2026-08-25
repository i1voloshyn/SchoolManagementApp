package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import java.util.List;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

@Repository
public class JdbcGroupRepository implements GroupRepository {
    private static final String INSERT_GROUP_QUERY =
            """
            INSERT INTO groups (name) VALUES (:name)
            """;
    private static final String DELETE_GROUP_QUERY =
            """
            DELETE from groups WHERE id = :id
            """;
    private static final String FIND_BY_MAX_STUDENT_COUNT_QUERY =
            """
            SELECT g.id, g.name
            FROM groups g
            LEFT JOIN students s ON s.group_id = g.id
            GROUP BY g.id, g.name
            HAVING COUNT(s.id) <= :maximumStudentCount;
            """;
    private static final String FIND_ALL_GROUPS_QUERY =
            """
            SELECT id, name FROM groups
            """;
    private static final RowMapper<Group> GROUP_MAPPER =
            (rs, rowNums) -> new Group(rs.getLong("id"), rs.getString("name"));

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public JdbcGroupRepository(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            JdbcTemplate jdbcTemplate,
            TransactionTemplate transactionTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Group save(Group group) {
        return transactionTemplate.execute(
                status -> {
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    int affectedRows =
                            namedParameterJdbcTemplate.update(
                                    INSERT_GROUP_QUERY,
                                    new MapSqlParameterSource("name", group.getName()),
                                    keyHolder,
                                    new String[] {"id"});
                    validateQuery(INSERT_GROUP_QUERY, 1, affectedRows);

                    Long generatedId = keyHolder.getKeyAs(Long.class);
                    group.setId(generatedId);

                    return group;
                });
    }

    @Override
    public void delete(Long id) {
        transactionTemplate.executeWithoutResult(
                status -> {
                    int affectedRows =
                            namedParameterJdbcTemplate.update(
                                    DELETE_GROUP_QUERY, new MapSqlParameterSource("id", id));
                    validateQuery(DELETE_GROUP_QUERY, 1, affectedRows);
                });
    }

    @Override
    public List<Group> findAll() {
        return jdbcTemplate.query(FIND_ALL_GROUPS_QUERY, GROUP_MAPPER);
    }

    @Override
    public List<Group> findByMaximumStudentCount(int maximumStudentCount) {
        return namedParameterJdbcTemplate.query(
                FIND_BY_MAX_STUDENT_COUNT_QUERY,
                new MapSqlParameterSource("maximumStudentCount", maximumStudentCount),
                GROUP_MAPPER);
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual);
        }
    }
}
