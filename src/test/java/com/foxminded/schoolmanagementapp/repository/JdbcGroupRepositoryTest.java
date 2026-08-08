package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcGroupRepository.class)
@Testcontainers
class JdbcGroupRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired
    GroupRepository repository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void save_shouldSaveAndReturnGroup_withGeneratedId() {
        Group groupToSave = new Group(null, "Test Group");

        Group saved = repository.save(groupToSave);

        Group actual = jdbcTemplate.queryForObject("SELECT id,name FROM groups WHERE id = ?",
                (rs, rn) -> {
                    Long id = rs.getLong(1);
                    String name = rs.getString(2);
                    return new Group(id, name);
                }, saved.getId());

        assertThat(actual.getName()).isEqualTo(groupToSave.getName());
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/groups/insert_five_groups.sql"})
    @Test
    void delete_shouldDeleteExpectedGroup() {
        Long groupIdToDelete = jdbcTemplate.queryForObject("SELECT id FROM groups LIMIT 1",
                Long.class
        );

        repository.delete(groupIdToDelete);
        Long remainingGroups = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM groups WHERE id = ?",
                Long.class, groupIdToDelete);

        assertThat(remainingGroups).isZero();
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/groups/insert_five_groups.sql"})
    @Test
    void findAll_shouldReturnAllGroups_fromDataBase() {
        List<String> expectedGroupNames =
                List.of("Group A", "Group B", "Group C", "Group D", "Group E");

        List<Group> actual = repository.findAll();

        assertThat(actual)
                .extracting(Group::getName)
                .containsExactlyInAnyOrderElementsOf(expectedGroupNames);
    }

    @Sql("/fixtures/clean_up.sql")
    @Test
    void findAll_shouldReturnEmptyList_whenDBIsEmpty() {
        List<Group> actual = repository.findAll();

        assertThat(actual).isEmpty();
    }


    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/groups/insert_five_groups.sql"})
    @Test
    @DisplayName("delete_shouldThrown_JdbcUpdateAffectedIncorrectNumberOfRowsException_forGroupId_thatDoNotExist")
    void delete_shouldThrowException() {
        assertThatException().isThrownBy(() -> repository.delete(-1L))
                .isInstanceOf(JdbcUpdateAffectedIncorrectNumberOfRowsException.class);
    }

    @Sql(value = {"/fixtures/clean_up.sql",
            "/fixtures/groups/groups_with_different_student_counts.sql"})
    @Test
    void findByMaximumStudentCount_shouldReturnListWithExpectedGroups() {
        int maximumStudentCount = 3;
        List<String> expectedGroupNames =
                List.of("Group A", "Group B", "Group C", "Group D");

        List<Group> actual = repository.findByMaximumStudentCount(maximumStudentCount);

        assertThat(actual)
                .extracting(Group::getName)
                .containsExactlyInAnyOrderElementsOf(expectedGroupNames);
    }
}