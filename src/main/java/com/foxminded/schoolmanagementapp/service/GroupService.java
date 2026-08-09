package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public List<Group> findByMaximumStudentCount(int studentCount) {
        if (studentCount < 0) {
            throw new IllegalArgumentException("Maximum student count must not be negative");
        }
        return groupRepository.findByMaximumStudentCount(studentCount);
    }
}
