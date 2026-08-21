package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.model.Enrollment;
import com.foxminded.schoolmanagementapp.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    private static final long STUDENT_ID = 1L;
    private static final long COURSE_ID = 10L;

    @Mock
    EnrollmentRepository enrollmentRepository;
    @InjectMocks
    EnrollmentService service;

    @Test
    void addStudentToCourse_shouldCreateNewEnrollment() {
        service.addStudentToCourse(STUDENT_ID, COURSE_ID);

        verify(enrollmentRepository).enroll(STUDENT_ID, COURSE_ID);
    }

    @Test
    void addStudentsToCourses_shouldCreateEnrollmentBatch() {
        List<Enrollment> enrollments = List.of(
                new Enrollment(STUDENT_ID, COURSE_ID),
                new Enrollment(2L, COURSE_ID)
        );

        service.addStudentsToCourses(enrollments);

        verify(enrollmentRepository).enrollAll(enrollments);
    }

    @Test
    void removeStudentFromCourse_shouldRemoveEnrollment() {
        service.removeStudentFromCourse(STUDENT_ID, COURSE_ID);

        verify(enrollmentRepository).remove(STUDENT_ID, COURSE_ID);
    }

}
