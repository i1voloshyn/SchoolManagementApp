package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.model.Enrollment;
import com.foxminded.schoolmanagementapp.repository.EnrollmentRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public void addStudentToCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        enrollmentRepository.enroll(studentId, courseId);
    }

    public void addStudentsToCourses(List<Enrollment> enrollments) {
        if (enrollments == null) {
            throw new IllegalArgumentException("Enrollments must not be null");
        }

        enrollments.forEach(
                enrollment -> {
                    if (enrollment == null) {
                        throw new IllegalArgumentException("Enrollment must not be null");
                    }
                    validateIds(enrollment.studentId(), enrollment.courseId());
                });

        enrollmentRepository.enrollAll(enrollments);
    }

    public void removeStudentFromCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        enrollmentRepository.remove(studentId, courseId);
    }

    private void validateIds(Long studentId, Long courseId) {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }
    }
}
