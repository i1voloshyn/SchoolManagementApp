package com.foxminded.schoolmanagementapp.exception;

public class EnrollmentNotFoundException extends SchoolManagementException {
    public EnrollmentNotFoundException(Long studentId, Long courseId) {
        super(
                "Enrollment was not found for student %d and course %d"
                        .formatted(studentId, courseId)
        );
    }
}
