package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);

    Course update(Course course);

    void enroll(Long studentId, Long courseId);

    void enrollAll(List<Enrollment> enrollments);

    void removeEnrollment(Long studentId, Long courseId);

    boolean enrollmentExist(Long studentId, Long courseId);

    void delete(Long id);

    List<Course> findAll();

    Optional<Course> findById(Long id);

    Optional<Course> findByName(String name);

    List<Course> findByStudentId(Long studentId);
}
