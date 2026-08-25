package com.foxminded.schoolmanagementapp.util.assignment;

import com.foxminded.schoolmanagementapp.config.GroupAssignmentProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RandomGroupAssignmentRule implements GroupAssignmentRule {
    private static final int NULL_INJECTION_INTERVAL = 4;
    private final Faker faker;
    private final GroupAssignmentProperties properties;

    @Override
    public List<Long> apply(List<Long> groupsId, int studentsCount) {
        List<Long> assignmentIds = new ArrayList<>();

        for (long id : groupsId) {
            int idRepetition = generateIdsRepetition(assignmentIds.size(), studentsCount);
            assignIds(idRepetition, assignmentIds, id);

            if (assignmentIds.size() == studentsCount) {
                return assignmentIds;
            }
        }

        while (assignmentIds.size() < studentsCount) {
            assignmentIds.add(null);
        }

        Collections.shuffle(assignmentIds);
        return assignmentIds;
    }

    private int generateIdsRepetition(int assignmentsCount, int studentsCount) {
        int idRepetition =
                faker.number()
                        .numberBetween(properties.minStudents(), properties.maxStudents() + 1);

        boolean assignmentsCountInRange = (assignmentsCount + idRepetition) < studentsCount;

        return assignmentsCountInRange ? idRepetition : (studentsCount - assignmentsCount);
    }

    private void assignIds(int idRepetition, List<Long> assignmentIds, Long id) {
        IntStream.range(0, idRepetition)
                .mapToObj(index -> shouldInjectNull(index) ? null : id)
                .forEach(assignmentIds::add);
    }

    private boolean shouldInjectNull(int index) {
        return (index + 1) % NULL_INJECTION_INTERVAL == 0;
    }
}
