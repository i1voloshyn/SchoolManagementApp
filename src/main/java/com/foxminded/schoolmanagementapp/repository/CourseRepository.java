package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);

    void delete(Long id);

    List<Course> findAll();

    Optional<Course> findById(Long id);

    Optional<Course> findByName(String name);

    List<Course> findByStudentId(Long studentId);
}
