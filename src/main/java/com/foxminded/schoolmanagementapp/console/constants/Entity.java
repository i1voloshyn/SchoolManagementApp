package com.foxminded.schoolmanagementapp.console.constants;

import lombok.Getter;

@Getter
public enum Entity {
    STUDENT("Student");

    private final String value;

    Entity(String value) {
        this.value = value;
    }
}
