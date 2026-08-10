package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.CourseDto;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseService service;

    @Test
    void createCourse_shouldReturnSavedCourseDto() {
        CourseDto request = new CourseDto(null, "Java", "Java programming course");
        Course courseToSave = new Course(null, "Java", "Java programming course");
        Course savedCourse = new Course(1L, "Java", "Java programming course");
        when(courseRepository.save(courseToSave)).thenReturn(savedCourse);

        CourseDto actual = service.createCourse(request);

        assertThat(actual).isEqualTo(new CourseDto(1L, "Java", "Java programming course"));
        verify(courseRepository).save(courseToSave);
    }

    @Test
    void createCourse_shouldRejectBlankName() {
        CourseDto request = new CourseDto(null, " ", "Java programming course");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.createCourse(request))
                .withMessage("Name cannot be blank");
        verifyNoInteractions(courseRepository);
    }

    @Test
    void createCourse_shouldRejectShortDescription() {
        CourseDto request = new CourseDto(null, "Java", "Too short");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.createCourse(request))
                .withMessage("Description must be at least 10 characters");
        verifyNoInteractions(courseRepository);
    }

    @Test
    void deleteCourse_shouldDeleteExistingCourse() {
        Long courseId = 1L;
        Course course = new Course(courseId, "Java", "Java programming course");
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        service.deleteCourse(courseId);

        verify(courseRepository).delete(courseId);
    }

    @Test
    void deleteCourse_shouldThrowException_whenCourseDoesNotExist() {
        Long courseId = 99L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CourseNotFoundException.class)
                .isThrownBy(() -> service.deleteCourse(courseId));
        verify(courseRepository, never()).delete(courseId);
    }

    @Test
    void deleteCourse_shouldRejectNonPositiveId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.deleteCourse(-1L))
                .withMessage("Course ID must be positive");
        verifyNoInteractions(courseRepository);
    }

    @Test
    void deleteCourse_shouldRejectNullId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.deleteCourse(null))
                .withMessage("Course ID must be positive");
        verifyNoInteractions(courseRepository);
    }

    @Test
    void findAll_shouldReturnCourseDtos() {
        List<Course> courses = List.of(
                new Course(1L, "Java", "Java programming course"),
                new Course(2L, "SQL", "Relational databases course")
        );
        when(courseRepository.findAll()).thenReturn(courses);

        List<CourseDto> actual = service.findAll();

        assertThat(actual).containsExactly(
                new CourseDto(1L, "Java", "Java programming course"),
                new CourseDto(2L, "SQL", "Relational databases course")
        );
        verify(courseRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoCoursesExist() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseDto> actual = service.findAll();

        assertThat(actual).isEmpty();
        verify(courseRepository).findAll();
    }
}
