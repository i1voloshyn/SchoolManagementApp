package com.foxminded.schoolmanagementapp.util.assignment;

import com.foxminded.schoolmanagementapp.config.GroupAssignmentProperties;
import net.datafaker.Faker;
import net.datafaker.providers.base.Number;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomGroupAssignmentRuleTest {

    @Mock
    private Faker faker;
    @Mock
    Number fakerNumber;
    @Mock
    private GroupAssignmentProperties properties;
    @InjectMocks
    private RandomGroupAssignmentRule rule;

    @Test
    void apply_shouldGenerateRandomListIds_withStudentsCountLength() {
        List<Long> ids = List.of(1L, 2L);
        int minStudents = 0;
        int maxStudents = 5;

        int random = 5;

        int studentsCount = 20;
        when(properties.minStudents()).thenReturn(minStudents);
        when(properties.maxStudents()).thenReturn(maxStudents);
        when(faker.number()).thenReturn(fakerNumber);
        when(fakerNumber.numberBetween(minStudents, maxStudents + 1)).thenReturn(random);

        List<Long> actual = rule.apply(ids, studentsCount);

        assertThat(actual).hasSize(studentsCount);
        assertThat(actual).allMatch(groupId -> groupId == null || ids.contains(groupId));
    }

}
