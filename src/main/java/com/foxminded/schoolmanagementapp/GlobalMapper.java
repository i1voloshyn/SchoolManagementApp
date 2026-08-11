package com.foxminded.schoolmanagementapp;

import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.CourseDto;
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
}
