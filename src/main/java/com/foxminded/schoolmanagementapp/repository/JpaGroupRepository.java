package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaGroupRepository implements GroupRepository {
    private static final String DELETE_GROUP_QUERY = "DELETE from Group g WHERE g.id = :id";
    private static final String FIND_BY_MAX_STUDENT_COUNT_QUERY =
            """
                    SELECT g
                    FROM Group g
                    LEFT JOIN Student s ON s.group.id = g.id
                    GROUP BY g.id, g.name
                    HAVING COUNT(s.id) <= :maximumStudentCount
                    """;
    private static final String FIND_ALL_GROUPS_QUERY = "SELECT g FROM Group g LEFT JOIN FETCH g.students";

    private final EntityManagerFactory emf;

    @Override
    public Group save(Group group) {
        return emf.callInTransaction(em -> {
            em.persist(group);
            return group;
        });
    }

    @Override
    public void delete(Long id) {
        emf.runInTransaction(em -> em.createQuery(DELETE_GROUP_QUERY)
                .setParameter("id", id)
                .executeUpdate()
        );
    }

    @Override
    public List<Group> findAll() {
        return emf.callInTransaction(em -> em.createQuery(FIND_ALL_GROUPS_QUERY, Group.class)
                .getResultList());
    }

    @Override
    public List<Group> findByMaximumStudentCount(int maximumStudentCount) {
        return emf.callInTransaction(em -> em.createQuery(FIND_BY_MAX_STUDENT_COUNT_QUERY, Group.class)
                .setParameter("maximumStudentCount", maximumStudentCount)
                .getResultList());
    }
}
