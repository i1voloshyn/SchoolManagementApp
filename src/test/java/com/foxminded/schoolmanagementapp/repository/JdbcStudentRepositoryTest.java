package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcStudentRepository.class)
@Testcontainers
class JdbcStudentRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired
    StudentsRepository repository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Sql("/fixtures/clean_up.sql")
    @Test
    void save_shouldSaveAndReturnStudent_withGeneratedId() {
        jdbcTemplate.update("INSERT INTO groups (name) VALUES (?)", "Test Group");
        Long groupId = jdbcTemplate.queryForObject(
                "SELECT id FROM groups WHERE name = ?",
                Long.class,
                "Test Group"
        );
        Student studentToSave = new Student(null, groupId, "Test", "Student");

        Student saved = repository.save(studentToSave);

        Student actual = jdbcTemplate.queryForObject(
                "SELECT id, group_id, first_name, last_name FROM students WHERE id = ?",
                (resultSet, rowNumber) -> new Student(
                        resultSet.getLong("id"),
                        resultSet.getLong("group_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                ),
                saved.getId()
        );

        assertThat(saved.getId()).isNotNull();
        assertThat(actual).isEqualTo(saved);
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/students/insert_five_students.sql"})
    @Test
    void delete_shouldDeleteExpectedStudent() {
        Long studentIdToDelete = jdbcTemplate.queryForObject(
                "SELECT id FROM students LIMIT 1",
                Long.class
        );

        repository.delete(studentIdToDelete);

        Long remainingStudents = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students WHERE id = ?",
                Long.class,
                studentIdToDelete
        );
        assertThat(remainingStudents).isZero();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void delete_shouldDeleteStudentEnrollments_butPreserveCourses() {
        Long coursesBeforeDelete = 3L;
        Long studentIdToDelete = jdbcTemplate.queryForObject(
                "SELECT id FROM students WHERE first_name = ?",
                Long.class,
                "John"
        );
        Long enrollmentsBeforeDelete = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students_courses WHERE student_id = ?",
                Long.class,
                studentIdToDelete
        );


        repository.delete(studentIdToDelete);

        Long remainingStudents = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students WHERE id = ?",
                Long.class,
                studentIdToDelete
        );
        Long remainingEnrollments = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students_courses WHERE student_id = ?",
                Long.class,
                studentIdToDelete
        );
        Long remainingCourses = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM courses",
                Long.class
        );

        assertThat(enrollmentsBeforeDelete).isEqualTo(2L);
        assertThat(remainingStudents).isZero();
        assertThat(remainingEnrollments).isZero();
        assertThat(remainingCourses).isEqualTo(coursesBeforeDelete);
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void delete_shouldThrowException_whenStudentDoesNotExist() {
        assertThatException()
                .isThrownBy(() -> repository.delete(-1L))
                .isInstanceOf(JdbcUpdateAffectedIncorrectNumberOfRowsException.class);
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/students/insert_five_students.sql"})
    @Test
    void findAll_shouldReturnAllStudents_fromDatabase() {
        List<String> expectedFirstNames = List.of("John", "Anna", "Mark", "Kate", "Emily");

        List<Student> actual = repository.findAll();

        assertThat(actual)
                .extracting(Student::getFirstName)
                .containsExactlyInAnyOrderElementsOf(expectedFirstNames);
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseIsEmpty() {
        List<Student> actual = repository.findAll();

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/students/insert_five_students.sql"})
    @Test
    void findById_shouldReturnExpectedStudent_whenStudentExists() {
        Long studentId = jdbcTemplate.queryForObject(
                "SELECT id FROM students WHERE first_name = ?",
                Long.class,
                "Mark"
        );

        Optional<Student> actual = repository.findById(studentId);

        assertThat(actual)
                .isPresent()
                .get()
                .extracting(Student::getFirstName, Student::getLastName)
                .containsExactly("Mark", "Brown");
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void findById_shouldReturnEmptyOptional_whenStudentDoesNotExist() {
        Optional<Student> actual = repository.findById(-1L);

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/students/insert_five_students.sql"})
    @Test
    void findByLastName_shouldReturnAllStudents_withExpectedLastName() {
        List<Student> actual = repository.findByLastName("Smith");

        assertThat(actual)
                .extracting(Student::getFirstName)
                .containsExactlyInAnyOrder("John", "Anna");
        assertThat(actual)
                .extracting(Student::getLastName)
                .containsOnly("Smith");
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/students/insert_five_students.sql"})
    @Test
    void findByLastName_shouldReturnEmptyList_whenLastNameDoesNotExist() {
        List<Student> actual = repository.findByLastName("Unknown");

        assertThat(actual).isEmpty();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void findByCourseId_shouldReturnAllStudents_enrolledInExpectedCourse() {
        Long courseId = jdbcTemplate.queryForObject(
                "SELECT id FROM courses WHERE name = ?",
                Long.class,
                "Java"
        );

        List<Student> actual = repository.findByCourseId(courseId);

        assertThat(actual)
                .extracting(Student::getFirstName)
                .containsExactlyInAnyOrder("John", "Anna");
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/enrollments/students_with_courses.sql"})
    @Test
    void findByCourseId_shouldReturnEmptyList_whenCourseHasNoEnrollments() {
        List<Student> actual = repository.findByCourseId(-1L);

        assertThat(actual).isEmpty();
    }
}
