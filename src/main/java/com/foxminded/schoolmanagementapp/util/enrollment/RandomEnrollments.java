package com.foxminded.schoolmanagementapp.util.enrollment;

import com.foxminded.schoolmanagementapp.config.StudentCoursesAssignmentProperties;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Component
public class RandomEnrollments implements EnrollmentsRule {
    private final Faker faker;
    private final StudentCoursesAssignmentProperties properties;

    @Override
    public List<Enrollment> apply(List<StudentDto> students, List<CourseDto> courses) {
        Objects.requireNonNull(students, "Students must not be null");
        Objects.requireNonNull(courses, "Courses must not be null");

        if (students.isEmpty()) {
            return List.of();
        }

        List<Long> studentIds = extractStudentIds(students);
        List<Long> courseIds = extractCourseIds(courses);
        validateAvailableCourses(courseIds.size());

        return studentIds.stream()
                .flatMap(studentId -> enrollStudent(studentId, courseIds).stream())
                .toList();
    }

    private List<Enrollment> enrollStudent(Long studentId, List<Long> courseIds) {
        int enrollmentCount = faker.number().numberBetween(properties.minCourses(), properties.maxCourses()+1);
        List<Long> shuffledIds = new ArrayList<>(courseIds);
        Collections.shuffle(shuffledIds);

        return shuffledIds.stream()
                .limit(enrollmentCount)
                .map(courseId -> new Enrollment(studentId, courseId))
                .toList();
    }

    private List<Long> extractStudentIds(List<StudentDto> students) {
        return students.stream()
                .map(StudentDto::id)
                .toList();
    }

    private List<Long> extractCourseIds(List<CourseDto> courses) {
        return courses.stream()
                .map(CourseDto::id)
                .toList();
    }


    private void validateAvailableCourses(int coursesCount) {
        if (coursesCount < properties.maxCourses()) {
            throw new IllegalArgumentException(
                    "At least %d courses are required".formatted(properties.maxCourses())
            );
        }
    }
}
