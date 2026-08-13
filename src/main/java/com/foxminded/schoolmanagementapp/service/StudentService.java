package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.GlobalMapper;
import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {
    private final StudentsRepository studentsRepository;
    private final CourseRepository courseRepository;
    private final GlobalMapper mapper;

    public List<StudentDto> findStudentsByCourseName(String courseName) {
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name must not be blank");
        }

        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with name: " + courseName));

        return studentsRepository.findByCourseId(course.getId())
                .stream()
                .map(mapper::toStudentDto)
                .toList();
    }

    public StudentDto addStudent(StudentDto dto) {
        validateNewStudent(dto);
        return mapper.toStudentDto(studentsRepository.save(mapper.toStudent(dto)));
    }

    public List<StudentDto> addStudents(List<StudentDto> students) {
        if (students == null) {
            throw new IllegalArgumentException("Students must not be null");
        }

        students.forEach(this::validateNewStudent);

        return studentsRepository.saveAll(
                        students.stream()
                                .map(mapper::toStudent)
                                .toList()
                ).stream()
                .map(mapper::toStudentDto)
                .toList();
    }

    public void deleteStudent(Long studentId) {
        validateStudentId(studentId);
        if (studentsRepository.findById(studentId).isEmpty()) {
            throw new StudentNotFoundException(studentId);
        }
        studentsRepository.delete(studentId);
    }

    private void validateNewStudent(StudentDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Student must not be null");
        }
        if (dto.id() != null) {
            throw new IllegalArgumentException("A new student must not have an ID");
        }
        if (dto.firstName() == null || dto.firstName().isBlank()) {
            throw new IllegalArgumentException("Student first name must not be blank");
        }
        if (dto.lastName() == null || dto.lastName().isBlank()) {
            throw new IllegalArgumentException("Student last name must not be blank");
        }
        if (dto.groupId() != null && dto.groupId() <= 0) {
            throw new IllegalArgumentException("Group ID must be positive or absent");
        }
    }

    private void validateStudentId(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }
    }
}
