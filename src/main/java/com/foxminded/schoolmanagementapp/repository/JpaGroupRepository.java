package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaGroupRepository implements GroupRepository {
    private static final String FIND_BY_MAX_STUDENT_COUNT_QUERY =
            """
                    SELECT g
                    FROM Group g
                    LEFT JOIN g.students s
                    GROUP BY g.id, g.name
                    HAVING COUNT(s.id) <= :maximumStudentCount
                    """;
    private static final String FIND_ALL_GROUPS_QUERY = "SELECT g FROM Group g";

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
        emf.runInTransaction(em -> {
                    Group group = em.find(Group.class, id);
                    if (group == null) {
                        throw new IllegalArgumentException("Group not found with ID: " + id);
                    }
                    em.remove(group);
                }
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
