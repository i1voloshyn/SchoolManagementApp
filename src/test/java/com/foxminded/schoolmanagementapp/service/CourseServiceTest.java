package com.foxminded.schoolmanagementapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.mapper.CourseMapper;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseService service;
    @Spy
    private CourseMapper courseMapper = Mappers.getMapper(CourseMapper.class);

    @Test
    void createCourse_shouldReturnSavedCourseDto() {
        CourseDto request = new CourseDto(null, "Java", "Java programming course");
        Course courseToSave =
                Course.builder()
                        .id(null)
                        .name("Java")
                        .description("Java programming course")
                        .build();
        Course savedCourse =
                Course.builder().id(1L).name("Java").description("Java programming course").build();
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        CourseDto actual = service.createCourse(request);

        assertThat(actual).isEqualTo(new CourseDto(1L, "Java", "Java programming course"));
        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(courseCaptor.capture());
        assertThat(courseCaptor.getValue())
                .extracting(Course::getId, Course::getName, Course::getDescription)
                .containsExactly(
                        courseToSave.getId(),
                        courseToSave.getName(),
                        courseToSave.getDescription());
        verify(courseMapper).toCourse(request);
        verify(courseMapper).toCourseDto(savedCourse);
    }

    @Test
    void createCourse_shouldRejectBlankName() {
        CourseDto request = new CourseDto(null, " ", "Java programming course");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.createCourse(request))
                .withMessage("Name cannot be blank");
        verifyNoInteractions(courseRepository, courseMapper);
    }

    @Test
    void createCourse_shouldRejectShortDescription() {
        CourseDto request = new CourseDto(null, "Java", "Too short");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.createCourse(request))
                .withMessage("Description must be at least 10 characters");
        verifyNoInteractions(courseRepository, courseMapper);
    }

    @Test
    void deleteCourse_shouldDeleteExistingCourse() {
        Long courseId = 1L;

        service.deleteCourse(courseId);

        verify(courseRepository).delete(courseId);
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
        List<Course> courses =
                List.of(
                        Course.builder()
                                .id(1L)
                                .name("Java")
                                .description("Java programming course")
                                .build(),
                        Course.builder()
                                .id(2L)
                                .name("SQL")
                                .description("Relational databases course")
                                .build());
        when(courseRepository.findAll()).thenReturn(courses);

        List<CourseDto> actual = service.findAll();

        assertThat(actual)
                .containsExactly(
                        new CourseDto(1L, "Java", "Java programming course"),
                        new CourseDto(2L, "SQL", "Relational databases course"));
        verify(courseRepository).findAll();
        courses.forEach(course -> verify(courseMapper).toCourseDto(course));
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoCoursesExist() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseDto> actual = service.findAll();

        assertThat(actual).isEmpty();
        verify(courseRepository).findAll();
        verifyNoInteractions(courseMapper);
    }
}
