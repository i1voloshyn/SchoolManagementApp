package com.foxminded.schoolmanagementapp.console.constants;

import lombok.Getter;

@Getter
public enum Prompt {
    COURSE_ID("Enter course ID:"),
    DELETE_STUDENT_CONFIRMATION(
            "Permanently delete student? Type '%s' to delete, '%s' to cancel"),
    DELETE_STUDENT_FROM_COURSE_CONFIRMATION(
            "Permanently delete student from course? Type '%s' to delete, '%s' to cancel"),
    FIRST_NAME("Enter student first name:"),
    LAST_NAME("Enter student last name:"),
    STUDENT_ID("Enter student ID:");

    private final String value;

    Prompt(String value) {
        this.value = value;
    }

}
