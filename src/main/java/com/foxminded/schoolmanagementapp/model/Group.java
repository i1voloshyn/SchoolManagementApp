package com.foxminded.schoolmanagementapp.model;

import jakarta.annotation.Nullable;

public record Group(
        @Nullable Integer groupId,
        String name
) {
}
