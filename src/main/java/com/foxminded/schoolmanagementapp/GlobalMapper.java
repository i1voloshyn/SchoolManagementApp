package com.foxminded.schoolmanagementapp;

import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class GlobalMapper {
    public Course toCourse(CourseDto request) {
        return new Course(null, request.name(), request.description());
    }

    public CourseDto toCourseDto(Course course) {
        return new CourseDto(course.getId(), course.getName(), course.getDescription());
    }

    public Student toStudent(StudentDto request) {
        return new Student(
                request.id(),
                request.groupId(),
                request.firstName(),
                request.lastName()
        );
    }

    public StudentDto toStudentDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getGroupId(),
                student.getFirstName(),
                student.getLastName()
        );
    }
}
