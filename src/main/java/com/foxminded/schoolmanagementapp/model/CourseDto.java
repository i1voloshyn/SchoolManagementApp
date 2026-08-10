package com.foxminded.schoolmanagementapp.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record CourseDto(
        @Nullable Long id,
        @NonNull String name,
        @NonNull String description
) {
    public Course toEntity() {
        return new Course(
                null,
                name(),
                description()
        );
    }

    public static CourseDto toDto(Course course) {
        return new CourseDto(
                course.getId(),
                course.getName(),
                course.getDescription()
        );
    }
}
