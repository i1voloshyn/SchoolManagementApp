package com.foxminded.schoolmanagementapp.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class GeneratorPropertiesTest {

    @ParameterizedTest
    @CsvSource({"-1,10,20", "200,-1,10", "200,10,-1"})
    void dataGeneratorProperties_shouldRejectNegativeCounts(int studentsCount, int groupsCount, int coursesCount) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DataGeneratorProperties(studentsCount, groupsCount, coursesCount));
    }

    @ParameterizedTest
    @CsvSource({"-1,30", "20,10"})
    void groupAssignmentProperties_shouldRejectInvalidRange(int minStudents, int maxStudents) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new GroupAssignmentProperties(minStudents, maxStudents));
    }

    @ParameterizedTest
    @CsvSource({"3,2", "0,3", "2,2"})
    void studentCoursesAssignmentProperties_shouldRejectInvalidRange(int minCourses, int maxCourses) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new StudentCoursesAssignmentProperties(minCourses, maxCourses));
    }
}
