package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.mapper.CourseMapper;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.StudentRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final StudentRepository studentRepository;

    @Transactional
    public CourseDto createCourse(CourseDto courseRequest) {
        validateRequest(courseRequest);

        Course savedCourse = courseRepository.save(courseMapper.toCourse(courseRequest));

        return courseMapper.toCourseDto(savedCourse);
    }

    @Transactional
    public CourseDto updateCourse(CourseDto courseRequest) {
        validateRequest(courseRequest);
        validateCourseId(courseRequest.id());
        Course course = findById(courseRequest.id());

        course.setName(courseRequest.name());
        course.setDescription(courseRequest.description());

        return courseMapper.toCourseDto(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        validateCourseId(courseId);
        Course course = findById(courseId);

        courseRepository.delete(course);
    }

    @Transactional
    public void addStudentToCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        Course course = findById(courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        course.addStudent(student);
    }

    @Transactional
    public void addStudentsToCourses(List<Enrollment> enrollments) {
        if (enrollments == null) {
            throw new IllegalArgumentException("Enrollments must not be null");
        }

        Long[] courseIds = new Long[enrollments.size()];
        Long[] studentsIds = new Long[enrollments.size()];

        for (int i = 0; i < enrollments.size(); i++) {
            var enrollment = enrollments.get(i);
            courseIds[i] = enrollment.courseId();
            studentsIds[i] = enrollment.studentId();
        }

        courseRepository.enrollAll(courseIds, studentsIds);
    }

    @Transactional
    public void removeStudentFromCourse(Long studentId, Long courseId) {
        validateIds(studentId, courseId);
        Course course = findById(courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        course.removeStudent(student);
    }

    public List<CourseDto> findAll() {
        return courseRepository.findAll().stream().map(courseMapper::toCourseDto).toList();
    }

    public boolean hasData(){
        return courseRepository.hasData();
    }

    private Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
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

    private void validateIds(Long studentId, Long courseId) {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }
    }
}
