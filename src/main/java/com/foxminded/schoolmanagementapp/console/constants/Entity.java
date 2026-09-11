package com.foxminded.schoolmanagementapp.console.constants;

import lombok.Getter;

@Getter
public enum Entity {
    COURSE("Course"),
    STUDENT("Student");

    private final String value;

    Entity(String value) {
        this.value = value;
    }
}
