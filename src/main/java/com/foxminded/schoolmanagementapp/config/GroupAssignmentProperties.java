package com.foxminded.schoolmanagementapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "school.group-assignment")
public record GroupAssignmentProperties(
        int minStudents,
        int maxStudents
) {
    public GroupAssignmentProperties {
        if (minStudents < 0) {
            throw new IllegalArgumentException("Minimum students must not be negative");
        }
        if (maxStudents < minStudents) {
            throw new IllegalArgumentException("Maximum students must not be less than minimum students");
        }
    }
}
