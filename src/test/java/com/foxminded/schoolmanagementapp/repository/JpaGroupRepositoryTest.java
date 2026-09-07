package com.foxminded.schoolmanagementapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.foxminded.schoolmanagementapp.model.Group;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaGroupRepository.class)
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaGroupRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.4");

    @Autowired
    GroupRepository repository;
    @Autowired
    EntityManagerFactory emf;

    @Test
    void save_shouldSaveAndReturnGroup_withGeneratedId() {
        Group groupToSave = Group.builder()
                .id(null)
                .name("Test Group")
                .build();

        Group saved = repository.save(groupToSave);

        Group actual = emf.callInTransaction(em -> em.find(Group.class, saved.getId()));

        assertThat(actual.getName()).isEqualTo(groupToSave.getName());
    }

    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/groups/insert_five_groups.sql"})
    @Test
    void delete_shouldDeleteExpectedGroup() {
        Long groupIdToDelete =
                emf.callInTransaction(em -> em
                        .createQuery("SELECT g.id FROM Group g WHERE g.name = :name", Long.class)
                        .setParameter("name", "Group A")
                        .getSingleResult());

        repository.delete(groupIdToDelete);

        Long remainingGroups =
                emf.callInTransaction(em -> em.createQuery("SELECT COUNT(g) FROM Group g WHERE g.id = :id", Long.class)
                        .setParameter("id", groupIdToDelete)
                        .getSingleResult());

        assertThat(remainingGroups).isZero();
    }


    @Sql(value = {"/fixtures/clean_up.sql", "/fixtures/groups/insert_five_groups.sql"})
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

    @Sql(
            value = {
                    "/fixtures/clean_up.sql",
                    "/fixtures/groups/groups_with_different_student_counts.sql"
            })
    @Test
    void findByMaximumStudentCount_shouldReturnListWithExpectedGroups() {
        int maximumStudentCount = 3;
        List<String> expectedGroupNames = List.of("Group A", "Group B", "Group C", "Group D");

        List<Group> actual = repository.findByMaximumStudentCount(maximumStudentCount);

        assertThat(actual)
                .extracting(Group::getName)
                .containsExactlyInAnyOrderElementsOf(expectedGroupNames);
    }
}
