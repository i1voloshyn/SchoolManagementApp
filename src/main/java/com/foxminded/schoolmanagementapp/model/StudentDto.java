package com.foxminded.schoolmanagementapp.model;

import org.jspecify.annotations.Nullable;

public record StudentDto(
        @Nullable Long id,
        @Nullable Long groupId,
        String firstName,
        String lastName
) {
}
