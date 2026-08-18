package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Enrollment;

import java.util.List;

public interface EnrollmentRepository {
    void enroll(Long studentId, Long courseId);

    void enrollAll(List<Enrollment> enrollments);

    void remove(Long studentId, Long courseId);

    boolean exists(Long studentId, Long courseId);
}
