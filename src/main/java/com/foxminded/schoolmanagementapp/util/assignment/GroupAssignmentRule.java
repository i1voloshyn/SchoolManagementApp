package com.foxminded.schoolmanagementapp.util.assignment;

import java.util.List;

public interface GroupAssignmentRule {
    List<Long> apply(List<Long> groupIds, int studentsCount);
}
