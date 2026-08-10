package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.CourseDto;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional
    public CourseDto createCourse(CourseDto courseRequest) {
        if (courseRequest.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (courseRequest.description().length() <= 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters");
        }
        Course savedCourse = courseRepository.save(courseRequest.toEntity());

        return CourseDto.toDto(savedCourse);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }
        if (courseRepository.findById(courseId).isEmpty()) {
            throw new CourseNotFoundException(courseId);
        }

        courseRepository.delete(courseId);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> findAll() {
        return courseRepository.findAll().stream()
                .map(CourseDto::toDto)
                .toList();
    }
}
