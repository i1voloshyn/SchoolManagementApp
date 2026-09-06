package com.foxminded.schoolmanagementapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.foxminded.schoolmanagementapp.config.DataGeneratorProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(properties = "school.console.enabled=false")
@Testcontainers
@Sql(scripts = "/fixtures/clean_up.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InitialDataGeneratorIntegrationTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired
    private InitialDataGenerator dataGenerator;
    @Autowired
    DataGeneratorProperties properties;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void generateDataIfEmpty_shouldGenerateExpectedCounts() {
        boolean generated = dataGenerator.generateDataIfEmpty();

        int groupsCount = count("SELECT COUNT(*) FROM groups");
        int coursesCount = count("SELECT COUNT(*) FROM courses");
        int studentsCount = count("SELECT COUNT(*) FROM students");

        assertThat(generated).isTrue();
        assertThat(groupsCount).isEqualTo(properties.groupsCount());
        assertThat(coursesCount).isEqualTo(properties.coursesCount());
        assertThat(studentsCount).isEqualTo(properties.studentsCount());
    }

    @Test
    void generateDataIfEmpty_shouldGenerateCorrectGroupNames() {
        dataGenerator.generateDataIfEmpty();

        List<String> groupNames =
                jdbcTemplate.queryForList("SELECT name FROM groups", String.class);

        assertThat(groupNames).hasSize(10).allMatch(name -> name.matches("AA-\\d{2}"));
    }

    @Test
    void generateDataIfEmpty_shouldAssignOnlyExistingGroups() {
        dataGenerator.generateDataIfEmpty();

        List<Long> existingGroupIds =
                jdbcTemplate.queryForList("SELECT id FROM groups", Long.class);
        List<Long> assignedGroupIds =
                jdbcTemplate.queryForList(
                        """
                                SELECT group_id
                                FROM students
                                WHERE group_id IS NOT NULL
                                """,
                        Long.class);

        assertThat(assignedGroupIds).allMatch(existingGroupIds::contains);
    }

    @Test
    void generateDataIfEmpty_shouldEnrollEveryStudentInOneToThreeCourses() {
        dataGenerator.generateDataIfEmpty();

        List<Integer> courseCounts =
                jdbcTemplate.queryForList(
                        """
                                SELECT COUNT(*)
                                FROM students_courses
                                GROUP BY student_id
                                """,
                        Integer.class);

        assertThat(courseCounts).hasSize(properties.studentsCount());
        assertThat(courseCounts).allMatch(count -> count >= 1 && count <= 3);
    }

    @Test
    void generateDataIfEmpty_shouldNotDuplicateData_whenCalledTwice() {
        boolean firstResult = dataGenerator.generateDataIfEmpty();
        DatabaseCounts afterFirstRun = readCounts();

        boolean secondResult = dataGenerator.generateDataIfEmpty();
        DatabaseCounts afterSecondRun = readCounts();

        assertThat(firstResult).isTrue();
        assertThat(secondResult).isFalse();
        assertThat(afterSecondRun).isEqualTo(afterFirstRun);
    }

    private DatabaseCounts readCounts() {
        return new DatabaseCounts(
                count("SELECT COUNT(*) FROM groups"),
                count("SELECT COUNT(*) FROM courses"),
                count("SELECT COUNT(*) FROM students"),
                count("SELECT COUNT(*) FROM students_courses"));
    }

    private int count(String sql) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result == null ? 0 : result;
    }

    private record DatabaseCounts(int groups, int courses, int students, int enrollments) {
    }
}
