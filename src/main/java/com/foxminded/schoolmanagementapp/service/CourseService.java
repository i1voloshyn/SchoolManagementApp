package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.mapper.CourseMapper;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseDto createCourse(CourseDto courseRequest) {
        validateRequest(courseRequest);

        Course savedCourse = courseRepository.save(courseMapper.toCourse(courseRequest));

        return courseMapper.toCourseDto(savedCourse);
    }

    public CourseDto updateCourse(CourseDto courseRequest) {
        validateRequest(courseRequest);
        validateCourseId(courseRequest.id());

        Course updatedCourse = courseRepository.update(courseMapper.toCourse(courseRequest));

        return courseMapper.toCourseDto(updatedCourse);
    }

    public void deleteCourse(Long courseId) {
        validateCourseId(courseId);

        courseRepository.delete(courseId);
    }

    public List<CourseDto> findAll() {
        return courseRepository.findAll().stream().map(courseMapper::toCourseDto).toList();
    }

    private void validateRequest(CourseDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Course must not be null");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (request.description() == null || request.description().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters");
        }
    }

    private void validateCourseId(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }
    }
}
