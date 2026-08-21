package com.foxminded.schoolmanagementapp.util.assignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.config.GroupAssignmentProperties;
import java.util.List;
import net.datafaker.Faker;
import net.datafaker.providers.base.Number;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RandomGroupAssignmentRuleTest {

    @Mock private Faker faker;
    @Mock Number fakerNumber;

    @Test
    void apply_shouldGenerateRandomListIds_withStudentsCountLength() {
        List<Long> ids = List.of(1L, 2L);
        int minStudents = 0;
        int maxStudents = 5;

        int random = 5;

        int studentsCount = 20;
        GroupAssignmentProperties properties =
                new GroupAssignmentProperties(minStudents, maxStudents);
        RandomGroupAssignmentRule rule = new RandomGroupAssignmentRule(faker, properties);

        when(faker.number()).thenReturn(fakerNumber);
        when(fakerNumber.numberBetween(minStudents, maxStudents + 1)).thenReturn(random);

        List<Long> actual = rule.apply(ids, studentsCount);

        assertThat(actual).hasSize(studentsCount);
        assertThat(actual).allMatch(groupId -> groupId == null || ids.contains(groupId));
    }
}
