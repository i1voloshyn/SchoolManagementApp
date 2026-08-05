package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentsRepository {
    Student save(Student student);

    void delete(Long id);

    List<Student> findAll();

    Optional<Student> findById(Long id);

    List<Student> findByLastName(String lastName);
}
