package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {
    private final StudentsRepository studentsRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<Student> findStudentsByCourseName(String courseName) {
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name must not be blank");
        }

        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with name: " + courseName));

        return studentsRepository.findByCourseId(course.getId());
    }

    @Transactional
    public Student addStudent(Student student) {
        validateNewStudent(student);
        return studentsRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        validateStudentId(studentId);
        if (studentsRepository.findById(studentId).isEmpty()) {
            throw new StudentNotFoundException(studentId);
        }
        studentsRepository.delete(studentId);
    }

    private void validateNewStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student must not be null");
        }
        if (student.getId() != null) {
            throw new IllegalArgumentException("A new student must not have an ID");
        }
        if (student.getFirstName() == null || student.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Student first name must not be blank");
        }
        if (student.getLastName() == null || student.getLastName().isBlank()) {
            throw new IllegalArgumentException("Student last name must not be blank");
        }
        if (student.getGroupId() != null && student.getGroupId() <= 0) {
            throw new IllegalArgumentException("Group ID must be positive or absent");
        }
    }

    private void validateStudentId(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }
    }
}
