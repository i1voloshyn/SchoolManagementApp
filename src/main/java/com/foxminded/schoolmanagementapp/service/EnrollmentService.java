package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.exception.EnrollmentAlreadyExistsException;
import com.foxminded.schoolmanagementapp.exception.EnrollmentNotFoundException;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.EnrollmentRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void addStudentToCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        requireStudent(studentId);
        requireCourse(courseId);

        if (enrollmentRepository.exists(studentId, courseId)) {
            throw new EnrollmentAlreadyExistsException(studentId, courseId);
        }

        enrollmentRepository.enroll(studentId, courseId);
    }

    @Transactional
    public void removeStudentFromCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        requireStudent(studentId);
        requireCourse(courseId);

        if (!enrollmentRepository.exists(studentId, courseId)) {
            throw new EnrollmentNotFoundException(studentId, courseId);
        }

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

    private void requireStudent(Long id) {
        studentsRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    private void requireCourse(Long id) {
        courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }
}

