package com.foxminded.schoolmanagementapp.util.datagenerator;

import com.foxminded.schoolmanagementapp.model.Group;

import java.util.List;

public interface GroupsGenerator {
    List<Group> generateGroups(int groupsCount);
}
