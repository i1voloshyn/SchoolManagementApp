package com.foxminded.schoolmanagementapp.exception;

public abstract class SchoolManagementException extends RuntimeException {
    protected SchoolManagementException(String message) {
        super(message);
    }

    protected SchoolManagementException(String message, Throwable e) {
        super(message, e);
    }
}
