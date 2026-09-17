package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("""
            SELECT g
                    FROM Group g
                    LEFT JOIN g.students s
                    GROUP BY g.id, g.name
                    HAVING COUNT(s.id) <= :maximumStudentCount
            """)
    List<Group> findByMaximumStudentCount(int maximumStudentCount);

    @NativeQuery("SELECT EXISTS(SELECT 1 FROM groups)")
    boolean hasData();
}
