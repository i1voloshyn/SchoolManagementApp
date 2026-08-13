package com.foxminded.schoolmanagementapp.util.assignment;

import com.foxminded.schoolmanagementapp.config.GroupAssignmentProperties;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RandomGroupAssignmentRuleTest {

    private final Faker faker = new Faker();
    private final GroupAssignmentProperties properties = new GroupAssignmentProperties(0, 30);

    private final GroupAssignmentRule rule = new RandomGroupAssignmentRule(faker, properties);

    @Test
    void apply_shouldGenerateRandomListIds_withStudentsCountLength() {
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        int studentsCount = 200;
        List<Long> actual = rule.apply(ids, studentsCount);

        assertThat(actual).hasSize(studentsCount);
        assertThat(actual).allMatch(groupId -> groupId == null || ids.contains(groupId));
    }

}
