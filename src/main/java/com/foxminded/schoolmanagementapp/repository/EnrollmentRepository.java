package com.foxminded.schoolmanagementapp.repository;

public interface EnrollmentRepository {
    void enroll(Long studentId, Long courseId);

    void remove(Long studentId, Long courseId);

    boolean exists(Long studentId, Long courseId);
}
