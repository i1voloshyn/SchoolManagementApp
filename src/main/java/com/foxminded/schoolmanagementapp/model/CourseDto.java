package com.foxminded.schoolmanagementapp.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record CourseDto(
        @Nullable Long id,
        @NonNull String name,
        @NonNull String description
) {

}
