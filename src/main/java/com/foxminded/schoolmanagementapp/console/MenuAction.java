package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.exception.consoleException.InvalidMenuOptionException;

import java.util.Arrays;

public enum MenuAction {

    FIND_GROUPS_BY_MAX_STUDENT_COUNT(
            1,
            "Find groups by maximum student count"
    ),
    FIND_STUDENTS_BY_COURSE_NAME(
            2,
            "Find students by course name"
    ),
    VIEW_COURSES(
            3,
            "View all courses"
    ),
    VIEW_GROUPS(
            4,
            "View all groups"
    ),
    CREATE_STUDENT(
            5,
            "Create new student"
    ),
    DELETE_STUDENT(
            6,
            "Delete student"
    ),
    ADD_STUDENT_TO_COURSE(
            7,
            "Add student to course"
    ),
    EXIT(
            0,
            "Exit"
    );

    private final int number;
    private final String description;

    MenuAction(int number, String description) {
        this.number = number;
        this.description = description;
    }

    public int number() {
        return number;
    }

    public String description() {
        return description;
    }

    public static MenuAction fromNumber(int number) {
        return Arrays.stream(values())
                .filter(action -> action.number == number)
                .findFirst()
                .orElseThrow(
                        () -> new InvalidMenuOptionException(number)
                );
    }
}
