package com.foxminded.schoolmanagementapp.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class GeneratorPropertiesTest {

    @Test
    void dataGeneratorProperties_shouldRejectNegativeCounts() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DataGeneratorProperties(-1, 10, 10));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DataGeneratorProperties(200, -1, 10));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DataGeneratorProperties(200, 10, -1));
    }

    @Test
    void groupAssignmentProperties_shouldRejectInvalidRange() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new GroupAssignmentProperties(-1, 30));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new GroupAssignmentProperties(20, 10));
    }

    @Test
    void studentCoursesAssignmentProperties_shouldRejectInvalidRange() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new StudentCoursesAssignmentProperties(0, 3));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new StudentCoursesAssignmentProperties(3, 2));
    }
}
