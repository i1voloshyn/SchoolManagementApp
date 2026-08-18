package com.foxminded.schoolmanagementapp.util;

import com.foxminded.schoolmanagementapp.config.DataGeneratorProperties;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.util.assignment.GroupAssignmentRule;
import com.foxminded.schoolmanagementapp.util.datagenerator.StudentGenerator;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@AllArgsConstructor
@Component
public class FakerStudentsDataGenerator implements StudentGenerator {
    private final Faker faker;
    private final DataGeneratorProperties properties;
    private final GroupAssignmentRule assignmentRule;

    @Override
    public List<StudentDto> generateStudentsWithGroups(List<Long> groupIds) {
        List<Long> assignments = assignmentRule.apply(groupIds, properties.studentsCount());

        return IntStream.range(0, properties.studentsCount())
                .mapToObj(index -> new StudentDto(
                        null,
                        assignments.get(index),
                        faker.name().firstName(),
                        faker.name().lastName()
                ))
                .toList();
    }

}
