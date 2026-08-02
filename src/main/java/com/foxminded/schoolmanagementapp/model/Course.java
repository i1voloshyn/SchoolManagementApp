package com.foxminded.schoolmanagementapp.model;

import org.jspecify.annotations.Nullable;

public record Course(
        @Nullable Integer courseId,
        String name,
        String description
) {
}
