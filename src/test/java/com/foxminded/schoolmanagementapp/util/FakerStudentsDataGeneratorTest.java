package com.foxminded.schoolmanagementapp.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.foxminded.schoolmanagementapp.config.DataGeneratorProperties;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.util.assignment.GroupAssignmentRule;
import com.foxminded.schoolmanagementapp.util.datagenerator.StudentGenerator;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

class FakerStudentsDataGeneratorTest {

    @Test
    void generateStudents_shouldGenerateStudentsWithAssignmentsProvidedByRule() {
        DataGeneratorProperties properties = new DataGeneratorProperties(3, 2, 0);
        GroupAssignmentRule rule = (groupIds, studentsCount) -> Arrays.asList(1L, null, 2L);
        StudentGenerator generator = generator(properties, rule);

        List<StudentDto> actual = generator.generateStudentsWithGroups(List.of(1L, 2L));

        assertThat(actual)
                .hasSize(3)
                .allSatisfy(
                        student -> {
                            assertThat(student.id()).isNull();
                            assertThat(student.firstName()).isNotBlank();
                            assertThat(student.lastName()).isNotBlank();
                        });
        assertThat(actual).extracting(StudentDto::groupId).containsExactly(1L, null, 2L);
    }

    private StudentGenerator generator(
            DataGeneratorProperties properties, GroupAssignmentRule assignmentRule) {
        return new FakerStudentsDataGenerator(new Faker(new Random(1)), properties, assignmentRule);
    }
}
