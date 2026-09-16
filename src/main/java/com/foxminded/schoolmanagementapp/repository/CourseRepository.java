package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findCourseByName(String name);

    @Query("SELECT DISTINCT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(@Param("studentId") Long studentId);

    @Modifying
    @NativeQuery("""
            
            INSERT INTO students_courses (student_id, course_id)
                       SELECT * FROM unnest(CAST(:studentIds AS bigint[]), CAST(:courseIds AS bigint[]))
            """)
    void enrollAll(@Param("courseIds") Long[] courseIds, @Param("studentIds") Long[] studentIds);

    @NativeQuery("SELECT EXISTS(SELECT 1 FROM courses UNION ALL SELECT 1 from students_courses)")
    boolean hasData();
}
