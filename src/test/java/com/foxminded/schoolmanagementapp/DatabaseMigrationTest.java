package com.foxminded.schoolmanagementapp;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class DatabaseMigrationTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @BeforeAll
    void beforeAll() {
        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(),
                        POSTGRES.getUsername(),
                        POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
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
        String selectFromCoursesSql = """
                SELECT * FROM courses
                """;
        try (Connection jdbcConnection = POSTGRES.createConnection("");
             PreparedStatement statement = jdbcConnection.prepareStatement(selectFromCoursesSql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData actual = resultSet.getMetaData();

            assertThat(actual.getColumnCount()).isEqualTo(3);
            assertThat(actual.getColumnName(1)).isEqualTo("id");
            assertThat(actual.getColumnName(2)).isEqualTo("name");
            assertThat(actual.getColumnName(3)).isEqualTo("description");
        }
    }

    @Test
    void migration_shouldCreateStudentsTable_withExpectedColumns() throws SQLException {
        String selectFromStudentsSql = """
                SELECT * FROM students
                """;
        try (Connection jdbcConnection = POSTGRES.createConnection("");
             PreparedStatement statement = jdbcConnection.prepareStatement(selectFromStudentsSql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData actual = resultSet.getMetaData();

            assertThat(actual.getColumnCount()).isEqualTo(4);
            assertThat(actual.getColumnName(1)).isEqualTo("id");
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
            assertThat(actual.getColumnName(1)).isEqualTo("id");
            assertThat(actual.getColumnName(2)).isEqualTo("name");
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

    @Test
    void deleteStudent_shouldDeleteEnrollmentButPreserveCourse() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("")) {
            long courseId = insertCourse(connection);
            long studentId = insertStudent(connection);

            insertEnrollment(connection, studentId, courseId);

            assertThat(studentExists(connection, studentId)).isTrue();
            assertThat(courseExists(connection, courseId)).isTrue();
            assertThat(enrollmentExists(connection, studentId, courseId)).isTrue();

            deleteStudent(connection, studentId);

            assertThat(studentExists(connection, studentId)).isFalse();
            assertThat(enrollmentExists(connection, studentId, courseId)).isFalse();
            assertThat(courseExists(connection, courseId)).isTrue();
        }
    }

    private long insertCourse(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO courses (name, description)
                VALUES (?, ?)
                RETURNING id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Java");
            statement.setString(2, "Java course");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Course was not inserted");
                }

                return resultSet.getLong("id");
            }
        }
    }

    private long insertStudent(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO students (group_id, first_name, last_name)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setNull(1, java.sql.Types.INTEGER);
            statement.setString(2, "Joe");
            statement.setString(3, "Toronto");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Student was not inserted");
                }

                return resultSet.getLong("id");
            }
        }
    }

    private void insertEnrollment(
            Connection connection,
            long studentId,
            long courseId
    ) throws SQLException {
        String sql = """
                INSERT INTO students_courses (student_id, course_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            statement.setLong(2, courseId);

            int insertedRows = statement.executeUpdate();

            if (insertedRows != 1) {
                throw new SQLException("Student-course relationship was not inserted");
            }
        }
    }

    private void deleteStudent(
            Connection connection,
            long studentId
    ) throws SQLException {
        String sql = """
                DELETE FROM students
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);

            int deletedRows = statement.executeUpdate();

            if (deletedRows != 1) {
                throw new SQLException(
                        "Expected to delete one student, but deleted: " + deletedRows
                );
            }
        }
    }

    private void deleteCourse(
            Connection connection,
            long courseId
    ) throws SQLException {
        String sql = """
                DELETE FROM courses
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, courseId);

            int deletedRows = statement.executeUpdate();

            if (deletedRows != 1) {
                throw new SQLException(
                        "Expected to delete one course, but deleted: " + deletedRows
                );
            }
        }
    }

    private boolean studentExists(
            Connection connection,
            long studentId
    ) throws SQLException {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM students
                    WHERE id = ?
                )
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        }
    }

    private boolean courseExists(
            Connection connection,
            long courseId
    ) throws SQLException {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM courses
                    WHERE id = ?
                )
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        }
    }

    private boolean enrollmentExists(
            Connection connection,
            long studentId,
            long courseId
    ) throws SQLException {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM students_courses
                    WHERE student_id = ?
                      AND course_id = ?
                )
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            statement.setLong(2, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        }
    }
}
