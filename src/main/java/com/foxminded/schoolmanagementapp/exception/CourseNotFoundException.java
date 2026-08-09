package com.foxminded.schoolmanagementapp.exception;

public class CourseNotFoundException extends SchoolManagementException {
    public CourseNotFoundException(Long id) {
        super("Course not found with ID: " + id);
    }

    public CourseNotFoundException(String message) {
        super(message);
    }
}
