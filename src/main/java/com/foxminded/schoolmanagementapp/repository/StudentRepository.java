package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findStudentByLastName(String lastName);

    @NativeQuery("""
            SELECT s.id,s.first_name,s.last_name,s.group_id
            FROM students s
            JOIN students_courses sc
            ON s.id=sc.student_id
            JOIN courses c
            ON c.id=sc.course_id
            WHERE c.name=:name
            """)
    List<Student> findStudentsByCourseName(String name);
}
