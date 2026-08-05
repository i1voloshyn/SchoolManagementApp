package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcGroupRepository implements GroupRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final RowMapper<Group> GROUP_MAPPER = (rs, rowNums) -> new Group(
            rs.getLong("group_id"),
            rs.getString("group_name"));

    public JdbcGroupRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Group save(Group group) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int affectedRows = namedParameterJdbcTemplate.update(
                getInsertGroupQuery(),
                new MapSqlParameterSource("group_name", group.getName()),
                keyHolder,
                new String[]{"group_id"}
        );
        validateQuery(getInsertGroupQuery(), 1, affectedRows);

        Long generatedId = keyHolder.getKeyAs(Long.class);
        group.setId(generatedId);

        return group;
    }

    @Override
    public void delete(Long id) {
        int affectedRows = namedParameterJdbcTemplate.update(getDeleteGroupSql(),
                new MapSqlParameterSource("group_id", id)
        );
        validateQuery(getDeleteGroupSql(), 1, affectedRows);
    }

    @Override
    public List<Group> findByMaximumStudentCount(int maximumStudentCount) {
        return namedParameterJdbcTemplate.query(getFindByMaxStudentCountQuery(),
                new MapSqlParameterSource("maximumStudentCount", maximumStudentCount)
                , GROUP_MAPPER);
    }

    private String getInsertGroupQuery() {
        return """
                INSERT INTO groups (group_name) VALUES (:group_name)
                """;
    }

    private String getDeleteGroupSql() {
        return """
                DELETE from groups WHERE group_id = :group_id
                """;
    }

    private String getFindByMaxStudentCountQuery() {
        return """
                SELECT g.group_id, g.group_name
                FROM groups g
                LEFT JOIN students s ON s.group_id = g.group_id
                GROUP BY g.group_id, g.group_name
                HAVING COUNT(s.student_id) <= :maximumStudentCount;
                """;
    }

    private void validateQuery(String query, int expected, int actual) {
        if (actual != expected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query, expected, actual
            );
        }
    }
}
