package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.GlobalMapper;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@AllArgsConstructor
@Service
public class StudentService {
    private final StudentsRepository studentsRepository;
    private final GlobalMapper mapper;

    public List<StudentDto> findStudentsByCourseName(String courseName) {
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name must not be blank");
        }

        return studentsRepository.findByCourseName(courseName).stream()
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

        return studentsRepository
                .saveAll(students.stream().map(mapper::toStudent).toList())
                .stream()
                .map(mapper::toStudentDto)
                .toList();
    }

    public void deleteStudent(Long studentId) {
        validateStudentId(studentId);

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
