package com.foxminded.schoolmanagementapp.util.assignment;

import com.foxminded.schoolmanagementapp.config.GroupAssignmentProperties;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Component
public class RandomGroupAssignmentRule implements GroupAssignmentRule {
    private final Faker faker;
    private final GroupAssignmentProperties properties;

    @Override
    public List<Long> apply(List<Long> groupsId, int studentsCount) {
        int idsSum = 0;
        List<Long> assignmentIds = new ArrayList<>();

        for (long id : groupsId) {
            int idRepetition = generateIdsRepetition(idsSum, studentsCount);
            assignIds(idRepetition, assignmentIds, id);

            idsSum += idRepetition;

            if (idsSum == studentsCount) {
                return assignmentIds;
            }
        }

        if (idsSum < studentsCount) {
            for (int i = 0; i < studentsCount - idsSum; i++) {
                assignmentIds.add(null);
            }
        }

        Collections.shuffle(assignmentIds);
        return assignmentIds;
    }

    private int generateIdsRepetition(int idsSum, int studentsCount) {
        int idRepetition = faker.number().numberBetween(properties.minStudents(),
                properties.maxStudents() + 1);
        if ((idsSum + idRepetition) > studentsCount) {
            idRepetition = studentsCount - idsSum;
        }
        return idRepetition;
    }

    private void assignIds(int idRepetition, List<Long> assignmentIds, Long id) {
        for (int i = 0; i < idRepetition; i++) {
            if (i % 4 == 3) {
                assignmentIds.add(null);
                continue;
            }
            assignmentIds.add(id);
        }
    }
}
