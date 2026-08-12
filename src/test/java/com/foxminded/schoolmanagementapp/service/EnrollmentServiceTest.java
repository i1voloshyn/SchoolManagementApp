package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.EnrollmentRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    private static final long STUDENT_ID = 1L;
    private static final long COURSE_ID = 10L;


    @Mock
    StudentsRepository studentsRepository;
    @Mock
    CourseRepository courseRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @InjectMocks
    EnrollmentService service;

    @Test
    void findStudentsByCourse_shouldThrowCourseNotFoundException_whenCourseDoNotExist() {
        String unknownCourse = "Unknown";
        when(courseRepository.findByName(unknownCourse))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(CourseNotFoundException.class)
                .isThrownBy(() -> service.findStudentsByCourseName(unknownCourse));
        verifyNoInteractions(studentsRepository);
    }

    @Test
    void findStudentsByCourse_shouldReturnListOfStudents() {
        String courseName = "Course";
        String description = "Description";
        Long courseId = 3L;
        List<Student> expected = students();

        when(courseRepository.findByName(courseName))
                .thenReturn(Optional.of(new Course(courseId, courseName, description)));
        when(studentsRepository.findByCourseId(courseId))
                .thenReturn(expected);

        List<Student> actual = service.findStudentsByCourseName(courseName);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void addStudentToCourse_shouldCreateNewEnrollment() {
        service.addStudentToCourse(STUDENT_ID, COURSE_ID);

        verify(enrollmentRepository).enroll(STUDENT_ID, COURSE_ID);
    }

    @Test
    void removeStudentFromCourse_shouldRemoveEnrollment() {
        service.removeStudentFromCourse(STUDENT_ID, COURSE_ID);

        verify(enrollmentRepository).remove(STUDENT_ID, COURSE_ID);
    }

    private List<Student> students() {
        return List.of(
                new Student(1L, 11L, "Joe", "Toronto"),
                new Student(2L, 12L, "Sam", "Nevada")
        );
    }


}