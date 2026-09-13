package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    void enrollAll(List<Enrollment> enrollments);

    void enroll(Long studentId, Long courseId);

    void removeEnrollment(Long studentId, Long courseId);

    boolean enrollmentExist(Long studentId, Long courseId);
}
