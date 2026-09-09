package com.foxminded.schoolmanagementapp;

import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.model.Course;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class GlobalMapper {
    public Course toCourse(CourseDto request) {
        return Course.builder()
                .id(request.id())
                .name(request.name())
                .description(request.description())
                .build();
    }

    public CourseDto toCourseDto(Course course) {
        return new CourseDto(course.getId(), course.getName(), course.getDescription());
    }
}
