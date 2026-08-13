package com.foxminded.schoolmanagementapp.util;

import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.util.datagenerator.GroupsGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultGroupsDataGeneratorTest {
    private final GroupsGenerator groupsGenerator = new DefaultGroupsDataGenerator();

    @Test
    void generateGroups_shouldGenerateUniqueGroups_matchingFormat() {
        List<Group> actual = groupsGenerator.generateGroups(10);

        assertThat(actual).
                hasSize(10)
                .allSatisfy(group -> {
                            assertThat(group.getId()).isNull();
                            assertThat(group.getName()).matches("[A-Z]{2}-\\d{2}");
                        }
                );
        assertThat(actual)
                .extracting(Group::getName)
                .doesNotHaveDuplicates();
    }


}
