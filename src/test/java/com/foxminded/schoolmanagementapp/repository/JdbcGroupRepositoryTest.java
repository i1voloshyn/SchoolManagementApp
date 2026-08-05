package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Name;
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
    GroupRepository dao;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void save_shouldSaveAndReturnGroup_withGeneratedId() {
        Group groupToSave = new Group(null, "Test Group");

        Group saved = dao.save(groupToSave);

        Group actual = jdbcTemplate.queryForObject("SELECT group_id,group_name FROM groups WHERE group_id = ?",
                (rs, rn) -> {
                    Long id = rs.getLong(1);
                    String name = rs.getString(2);
                    return new Group(id, name);
                }, saved.getId());

        assertThat(actual.getName()).isEqualTo(groupToSave.getName());
    }

    @Sql("/fixtures/groups/insert_five_groups.sql")
    @Test
    void delete_shouldDeleteExpectedGroup() {
        Long groupIdToDelete = jdbcTemplate.queryForObject(
                "SELECT group_id FROM groups where group_name = ?",
                (rs, rn) -> rs.getLong(1),
                "Group B"
        );

        dao.delete(groupIdToDelete);
        Long remainingGroups = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM groups WHERE group_id = ?",
                Long.class, groupIdToDelete);

        assertThat(remainingGroups).isZero();
    }


    @Sql("/fixtures/groups/insert_five_groups.sql")
    @Test
    @DisplayName("delete_shouldThrown_JdbcUpdateAffectedIncorrectNumberOfRowsException_forGroupId_thatDoNotExist")
    void delete_shouldThrowException() {
        assertThatException().isThrownBy(() -> dao.delete(-1L))
                .isInstanceOf(JdbcUpdateAffectedIncorrectNumberOfRowsException.class);
    }

    @Sql("/fixtures/groups/groups_with_different_student_counts.sql")
    @Test
    void findByMaximumStudentCount_shouldReturnListWithExpectedGroups() {
        int maximumStudentCount = 3;
        List<String> expectedGroupNames =
                List.of("Group A", "Group B", "Group C", "Group D");

        List<Group> actual = dao.findByMaximumStudentCount(maximumStudentCount);

        assertThat(actual)
                .extracting(Group::getName)
                .containsExactlyInAnyOrderElementsOf(expectedGroupNames);
    }
}