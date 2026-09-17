package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.mapper.StudentMapper;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.StudentRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public List<StudentDto> findStudentsByCourseName(String courseName) {
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name must not be blank");
        }

        return studentRepository.findStudentsByCourseName(courseName).stream()
                .map(studentMapper::toStudentDto)
                .toList();
    }

    @Transactional
    public StudentDto addStudent(StudentDto dto) {
        validateNewStudent(dto);
        return studentMapper.toStudentDto(studentRepository.save(studentMapper.toStudent(dto)));
    }

    @Transactional
    public List<StudentDto> addStudents(List<StudentDto> students) {
        if (students == null) {
            throw new IllegalArgumentException("Students must not be null");
        }

        students.forEach(this::validateNewStudent);

        List<Student> toSave = students.stream().map(studentMapper::toStudent).toList();

        return studentRepository
                .saveAll(toSave)
                .stream()
                .map(studentMapper::toStudentDto)
                .toList();
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        validateStudentId(studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        studentRepository.delete(student);
    }

    public boolean hasData(){
        return studentRepository.hasData();
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
