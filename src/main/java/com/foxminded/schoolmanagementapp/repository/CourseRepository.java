package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long>, EnrollmentRepository {
    Optional<Course> findCourseByName(String name);

    @Query("SELECT DISTINCT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(@Param("studentId") Long studentId);
}
