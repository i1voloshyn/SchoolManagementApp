package com.foxminded.schoolmanagementapp.util;

import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.util.datagenerator.GroupsGenerator;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class DefaultGroupsDataGenerator implements GroupsGenerator {
    private static final String GROUP_NAME_TEMPLATE = "AA-%02d";

    @Override
    public List<Group> generateGroups(int groupsCount) {
        return IntStream.range(0, groupsCount)
                .mapToObj(index -> Group.builder().name(String.format(GROUP_NAME_TEMPLATE, index + 1)).build())
                .toList();
    }
}
