package com.foxminded.schoolmanagementapp.repository;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class JpaSchoolDataStateRepository implements SchoolDataStateRepository {
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

    private final EntityManagerFactory emf;

    @Override
    public boolean hasData() {
        Boolean hasAnyData = (Boolean) emf.callInTransaction(
                em -> em.createNativeQuery(HAS_ANY_DATA_QUERY, Boolean.class)
                        .getSingleResult());

        return Boolean.TRUE.equals(hasAnyData);
    }
}
