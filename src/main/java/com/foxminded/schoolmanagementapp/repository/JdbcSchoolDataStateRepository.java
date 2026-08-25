package com.foxminded.schoolmanagementapp.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class JdbcSchoolDataStateRepository implements SchoolDataStateRepository {
    private static final String HAS_ANY_DATA_QUERY =
            """
            SELECT EXISTS (
                SELECT 1 FROM groups
                UNION ALL
                SELECT 1 FROM students
                UNION ALL
                SELECT 1 FROM courses
                UNION ALL
                SELECT 1 FROM students_courses
            )
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean hasData() {
        Boolean hasAnyData = jdbcTemplate.queryForObject(HAS_ANY_DATA_QUERY, Boolean.class);

        return Boolean.TRUE.equals(hasAnyData);
    }
}
