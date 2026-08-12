package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.GlobalMapper;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final GlobalMapper mapper;

    public CourseDto createCourse(CourseDto courseRequest) {
        validateRequest(courseRequest);

        Course savedCourse = courseRepository.save(mapper.toCourse(courseRequest));

        return mapper.toCourseDto(savedCourse);
    }

    public void deleteCourse(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }

        courseRepository.delete(courseId);
    }

    public List<CourseDto> findAll() {
        return courseRepository.findAll().stream()
                .map(mapper::toCourseDto)
                .toList();
    }

    private void validateRequest(CourseDto request) {
        if (request.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (request.description().length() <= 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters");
        }
    }
}
