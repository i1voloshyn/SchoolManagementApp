package com.foxminded.schoolmanagementapp.exception;

public class EnrollmentAlreadyExistsException extends SchoolManagementException {
    public EnrollmentAlreadyExistsException(Long studentId, Long courseId) {
        super(
                "Student %d is already enrolled in course %d"
                        .formatted(studentId, courseId)
        );
    }
}
