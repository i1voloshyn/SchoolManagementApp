package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Group;
import java.util.List;

public interface GroupRepository {
    Group save(Group group);

    void delete(Long id);

    List<Group> findAll();

    List<Group> findByMaximumStudentCount(int maximumStudentCount);
}
