package com.foxminded.schoolmanagementapp.console;

public enum ConsoleMessage {
    CANCEL_OPTION("N"),
    CONFIRMATION("Confirmation"),
    COURSE_ID("Course ID"),
    COURSE_ID_PROMPT("Enter course ID:"),
    COURSE_NAME("Course name"),
    DELETE_OPTION("Y"),
    DELETE_STUDENT_CONFIRMATION_PROMPT(
            "Permanently delete student? Type '%s' to delete, '%s' to cancel"),
    DELETE_STUDENT_FROM_COURSE_CONFIRMATION_PROMPT(
            "Permanently delete student from course? Type '%s' to delete, '%s' to cancel"),
    FIRST_NAME("First name"),
    FIRST_NAME_PROMPT("Enter student first name:"),
    LAST_NAME("Last name"),
    LAST_NAME_PROMPT("Enter student last name:"),
    MAXIMUM_STUDENT_COUNT("Maximum student count"),
    STUDENT("Student"),
    STUDENT_ID("Student ID"),
    STUDENT_ID_PROMPT("Enter student ID:");

    private final String text;

    ConsoleMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public String format(Object... arguments) {
        return text.formatted(arguments);
    }
}
