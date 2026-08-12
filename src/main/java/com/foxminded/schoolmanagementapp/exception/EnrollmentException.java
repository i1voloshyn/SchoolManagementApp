package com.foxminded.schoolmanagementapp.exception;

public class EnrollmentException extends SchoolManagementException {
    public EnrollmentException(String message, Throwable e) {
        super(message, e);
    }

    public EnrollmentException(String message) {
        super(message);
    }
}
