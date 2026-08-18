package com.foxminded.schoolmanagementapp.util.enrollment;

import com.foxminded.schoolmanagementapp.config.StudentCoursesAssignmentProperties;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import net.datafaker.Faker;
import net.datafaker.providers.base.Number;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomEnrollmentsTest {

    @Mock
    private Faker faker;
    @Mock
    private Number fakerNumber;
    @Mock
    private StudentCoursesAssignmentProperties properties;
    @InjectMocks
    private RandomEnrollments randomEnrollments;

    @Test
    void apply_shouldEnrollEveryStudentInConfiguredNumberOfDistinctCourses() {
        List<StudentDto> students = List.of(
                new StudentDto(1L, null, "John", "Smith"),
                new StudentDto(2L, null, "Anna", "Brown")
        );
        List<CourseDto> courses = List.of(
                new CourseDto(10L, "Java", "Java fundamentals"),
                new CourseDto(20L, "SQL", "SQL fundamentals"),
                new CourseDto(30L, "Spring", "Spring fundamentals")
        );
        int minCourses = 1;
        int maxCourses = 3;
        int enrollmentCount = 2;

        when(properties.minCourses()).thenReturn(minCourses);
        when(properties.maxCourses()).thenReturn(maxCourses);
        when(faker.number()).thenReturn(fakerNumber);
        when(fakerNumber.numberBetween(minCourses, maxCourses + 1)).thenReturn(enrollmentCount);

        List<Enrollment> actual = randomEnrollments.apply(students, courses);

        assertThat(actual)
                .extracting(Enrollment::studentId)
                .containsOnly(1L, 2L);
        assertThat(actual)
                .extracting(Enrollment::courseId)
                .allMatch(courseId -> List.of(10L, 20L, 30L).contains(courseId));

        students.forEach(student -> {
            List<Enrollment> studentEnrollments = actual.stream()
                    .filter(enrollment -> enrollment.studentId().equals(student.id()))
                    .toList();

            assertThat(studentEnrollments).hasSize(enrollmentCount);
            assertThat(studentEnrollments)
                    .extracting(Enrollment::courseId)
                    .doesNotHaveDuplicates();
        });
    }
}
