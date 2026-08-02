package com.foxminded.schoolmanagementapp;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseMigrationTest {

    private final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18.4");

    @BeforeAll
    void beforeAll() {
        POSTGRES.start();
        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(),
                        POSTGRES.getUsername(),
                        POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }

    @AfterAll
    void afterAll() {
        POSTGRES.stop();
    }

    @Test
    void migration_shouldCreateExpectedTables() throws SQLException {
        String queryTablesSql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'public'
                """;
        List<String> expectedTables = List.of("students", "courses", "groups", "students_courses", "flyway_schema_history");

        try (Connection jdbcConnection = POSTGRES.createConnection("");
             PreparedStatement statement = jdbcConnection.prepareStatement(queryTablesSql);
             ResultSet tables = statement.executeQuery()) {

            List<String> actualTables = new ArrayList<>();
            while (tables.next()) {
                actualTables.add(tables.getString("TABLE_NAME"));
            }

            assertThat(actualTables).containsExactlyInAnyOrderElementsOf(expectedTables);
        }
    }

    @Test
    void migration_shouldCreateCoursesTable_withExpectedColumns() throws SQLException {
        String selectFromCoursesSql = "SELECT * FROM courses";
        try (Connection jdbcConnection = POSTGRES.createConnection("");
             PreparedStatement statement = jdbcConnection.prepareStatement(selectFromCoursesSql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData actual = resultSet.getMetaData();

            assertThat(actual.getColumnCount()).isEqualTo(3);
            assertThat(actual.getColumnName(1)).isEqualTo("course_id");
            assertThat(actual.getColumnName(2)).isEqualTo("course_name");
            assertThat(actual.getColumnName(3)).isEqualTo("course_description");
        }
    }

    @Test
    void migration_shouldCreateStudentsTable_withExpectedColumns() throws SQLException {
        String selectFromStudentsSql = "SELECT * FROM students";
        try (Connection jdbcConnection = POSTGRES.createConnection("");
             PreparedStatement statement = jdbcConnection.prepareStatement(selectFromStudentsSql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData actual = resultSet.getMetaData();

            assertThat(actual.getColumnCount()).isEqualTo(4);
            assertThat(actual.getColumnName(1)).isEqualTo("student_id");
            assertThat(actual.getColumnName(2)).isEqualTo("group_id");
            assertThat(actual.getColumnName(3)).isEqualTo("first_name");
            assertThat(actual.getColumnName(4)).isEqualTo("last_name");
        }
    }

    @Test
    void migration_shouldCreateGroupsTable_withExpectedColumns() throws SQLException {
        String selectFromGroupsSql = "SELECT * FROM groups";
        try (Connection jdbcConnection = POSTGRES.createConnection("");
             PreparedStatement statement = jdbcConnection.prepareStatement(selectFromGroupsSql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData actual = resultSet.getMetaData();

            assertThat(actual.getColumnCount()).isEqualTo(2);
            assertThat(actual.getColumnName(1)).isEqualTo("group_id");
            assertThat(actual.getColumnName(2)).isEqualTo("group_name");
        }
    }

    @Test
    void migration_shouldCreateStudentsCoursesTable_withExpectedColumns() throws SQLException {
        String selectFromStudentsCoursesSql = "SELECT * FROM students_courses";
        try (Connection jdbcConnection = POSTGRES.createConnection("");
             PreparedStatement statement = jdbcConnection.prepareStatement(selectFromStudentsCoursesSql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData actual = resultSet.getMetaData();

            assertThat(actual.getColumnCount()).isEqualTo(2);
            assertThat(actual.getColumnName(1)).isEqualTo("student_id");
            assertThat(actual.getColumnName(2)).isEqualTo("course_id");
        }
    }
}
