package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.repository.GroupRepository;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GroupService {
    private static final Pattern GROUP_NAME_PATTERN = Pattern.compile("A{2}-\\d{2}");

    private final GroupRepository groupRepository;

    public Group createGroup(Group group) {
        validateNewGroup(group);
        return groupRepository.save(group);
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public List<Group> findByMaximumStudentCount(int studentCount) {
        if (studentCount < 0) {
            throw new IllegalArgumentException("Maximum student count must not be negative");
        }
        return groupRepository.findByMaximumStudentCount(studentCount);
    }

    private void validateNewGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group must not be null");
        }
        if (group.getId() != null) {
            throw new IllegalArgumentException("A new group must not have an ID");
        }
        if (group.getName() == null || !GROUP_NAME_PATTERN.matcher(group.getName()).matches()) {
            throw new IllegalArgumentException("Group name must match pattern AA-00");
        }
    }
}
