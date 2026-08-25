package com.foxminded.schoolmanagementapp.console.constants;

import lombok.Getter;

@Getter
public enum Field {
    CONFIRMATION("Confirmation"),
    COURSE_ID("Course ID"),
    COURSE_NAME("Course name"),
    FIRST_NAME("First name"),
    LAST_NAME("Last name"),
    MAXIMUM_STUDENT_COUNT("Maximum student count"),
    STUDENT_ID("Student ID");

    private final String value;

    Field(String value) {
        this.value = value;
    }
}
