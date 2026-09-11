package com.foxminded.schoolmanagementapp.console.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonPositiveNumberException;
import com.foxminded.schoolmanagementapp.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddStudentToCourseActionTest {
    private static final String STUDENT_ID = "Student ID";
    private static final String COURSE_ID = "Course ID";

    @Mock ConsoleInputReader reader;
    @Mock
    CourseService service;
    @Mock ConsoleView view;
    @InjectMocks AddStudentToCourseAction action;

    @Test
    void execute_shouldAddStudentToCourse() {
        long studentId = 1L;
        long courseId = 2L;

        when(reader.readPositiveLong(STUDENT_ID)).thenReturn(studentId);
        when(reader.readPositiveLong(COURSE_ID)).thenReturn(courseId);

        LoopStatus actual = action.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(view).promptForWriteOperationFlow("Enter student ID:");
        verify(reader).readPositiveLong(STUDENT_ID);
        verify(view).promptForWriteOperationFlow("Enter course ID:");
        verify(reader).readPositiveLong(COURSE_ID);
        verify(service).addStudentToCourse(studentId, courseId);
        verify(view).showSuccessMessageOnEnrollment();
    }

    @Test
    void execute_shouldNotAddStudentToCourse_whenStudentIdValidationFails() {
        var exception = new NonPositiveNumberException(STUDENT_ID, 0);

        when(reader.readPositiveLong(STUDENT_ID)).thenThrow(exception);

        assertThatExceptionOfType(NonPositiveNumberException.class)
                .isThrownBy(action::execute)
                .isSameAs(exception);

        verify(view).promptForWriteOperationFlow("Enter student ID:");
        verify(reader).readPositiveLong(STUDENT_ID);
        verifyNoInteractions(service);
        verify(view, never()).showSuccessMessageOnEnrollment();
        verifyNoMoreInteractions(view, reader);
    }

    @Test
    void execute_shouldNotAddStudentToCourse_whenCourseIdValidationFails() {
        long studentId = 1L;
        var exception = new NonPositiveNumberException(COURSE_ID, 0);

        when(reader.readPositiveLong(STUDENT_ID)).thenReturn(studentId);
        when(reader.readPositiveLong(COURSE_ID)).thenThrow(exception);

        assertThatExceptionOfType(NonPositiveNumberException.class)
                .isThrownBy(action::execute)
                .isSameAs(exception);

        verify(view).promptForWriteOperationFlow("Enter student ID:");
        verify(reader).readPositiveLong(STUDENT_ID);
        verify(view).promptForWriteOperationFlow("Enter course ID:");
        verify(reader).readPositiveLong(COURSE_ID);
        verifyNoInteractions(service);
        verify(view, never()).showSuccessMessageOnEnrollment();
        verifyNoMoreInteractions(view, reader);
    }
}
