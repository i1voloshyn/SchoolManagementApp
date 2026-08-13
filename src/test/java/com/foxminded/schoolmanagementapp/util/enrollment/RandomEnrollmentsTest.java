package com.foxminded.schoolmanagementapp.util.enrollment;

import com.foxminded.schoolmanagementapp.config.StudentCoursesAssignmentProperties;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class RandomEnrollmentsTest {

    private final RandomEnrollments randomEnrollments = new RandomEnrollments(
            new Faker(new Random(1)),
            new StudentCoursesAssignmentProperties(1, 3)
    );

    @Test
    void apply_shouldEnrollEveryStudentInOneToThreeDistinctCourses() {
        List<StudentDto> students = List.of(
                new StudentDto(1L, null, "John", "Smith"),
                new StudentDto(2L, null, "Anna", "Brown")
        );
        List<CourseDto> courses = List.of(
                new CourseDto(10L, "Java", "Java fundamentals"),
                new CourseDto(20L, "SQL", "SQL fundamentals"),
                new CourseDto(30L, "Spring", "Spring fundamentals")
        );

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

            assertThat(studentEnrollments).hasSizeBetween(1, 3);
            assertThat(studentEnrollments)
                    .extracting(Enrollment::courseId)
                    .doesNotHaveDuplicates();
        });
    }


}
