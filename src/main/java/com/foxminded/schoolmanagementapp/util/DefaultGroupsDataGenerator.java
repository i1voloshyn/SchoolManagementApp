package com.foxminded.schoolmanagementapp.util;

import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.util.datagenerator.GroupsGenerator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class DefaultGroupsDataGenerator implements GroupsGenerator {
    private static final String GROUP_NAME_TEMPLATE = "AA-%02d";

    @Override
    public List<Group> generateGroups(int groupsCount) {
        return IntStream.range(0, groupsCount)
                .mapToObj(index -> new Group(null, GROUP_NAME_TEMPLATE.formatted(index)))
                .toList();
    }
}
