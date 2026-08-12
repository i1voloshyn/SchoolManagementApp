package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.EnrollmentRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class EnrollmentService {
    private final StudentsRepository studentsRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<Student> findStudentsByCourseName(String courseName) {
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new CourseNotFoundException(("Course not found with name: " + courseName)));
        return studentsRepository.findByCourseId(course.getId());
    }

    public void addStudentToCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        enrollmentRepository.enroll(studentId, courseId);
    }

    public void removeStudentFromCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        enrollmentRepository.remove(studentId, courseId);
    }

    private void validateIds(Long studentId, Long courseId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }
        if (courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }
    }
}

