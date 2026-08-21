package com.foxminded.schoolmanagementapp.exception;

public class StudentNotFoundException extends SchoolManagementException {
    public StudentNotFoundException(Long id) {
        super("Student not found with ID: " + id);
    }
}
