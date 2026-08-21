package com.foxminded.schoolmanagementapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "school.data-generator")
public record DataGeneratorProperties(int studentsCount, int groupsCount, int coursesCount) {
    public DataGeneratorProperties {
        if (studentsCount < 0) {
            throw new IllegalArgumentException("Students count must not be negative");
        }
        if (groupsCount < 0) {
            throw new IllegalArgumentException("Groups count must not be negative");
        }
        if (coursesCount < 0) {
            throw new IllegalArgumentException("Courses count must not be negative");
        }
    }
}
