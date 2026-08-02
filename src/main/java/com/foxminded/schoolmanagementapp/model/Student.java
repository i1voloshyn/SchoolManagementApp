package com.foxminded.schoolmanagementapp.model;

import org.jspecify.annotations.Nullable;

public record Student(
        @Nullable Long studentId,
        @Nullable Integer groupId,
        String firstName,
        String lastName
) {
}
