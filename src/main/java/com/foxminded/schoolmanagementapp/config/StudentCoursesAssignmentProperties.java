package com.foxminded.schoolmanagementapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "school.student-courses-assignment")
public record StudentCoursesAssignmentProperties(
        int minCourses,
        int maxCourses
) {
    public StudentCoursesAssignmentProperties {
        if (minCourses < 1) {
            throw new IllegalArgumentException("Minimum courses must be positive");
        }
        if (maxCourses < minCourses) {
            throw new IllegalArgumentException("Maximum courses must not be less than minimum courses");
        }
    }
}
