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
import com.foxminded.schoolmanagementapp.exception.consoleException.InvalidConfirmationException;
import com.foxminded.schoolmanagementapp.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteStudentFromCourseActionTest {
    private static final String STUDENT_ID = "Student ID";
    private static final String COURSE_ID = "Course ID";
    private static final String CONFIRMATION = "Confirmation";
    private static final String DELETE = "Y";
    private static final String CANCEL = "N";
    private static final String CONFIRMATION_PROMPT =
            "Permanently delete student from course? Type '%s' to delete, '%s' to cancel"
                    .formatted(DELETE, CANCEL);

    @Mock
    CourseService service;
    @Mock ConsoleView view;
    @Mock ConsoleInputReader reader;
    @InjectMocks DeleteStudentFromCourseAction action;

    @Test
    void execute_shouldRemoveStudentFromCourse_whenRemovalIsConfirmed() {
        long studentId = 1L;
        long courseId = 2L;

        when(reader.readPositiveLong(STUDENT_ID)).thenReturn(studentId);
        when(reader.readPositiveLong(COURSE_ID)).thenReturn(courseId);
        when(reader.readRequiredText(CONFIRMATION)).thenReturn("Y");

        LoopStatus actual = action.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(view).promptForWriteOperationFlow("Enter student ID:");
        verify(reader).readPositiveLong(STUDENT_ID);
        verify(view).promptForWriteOperationFlow("Enter course ID:");
        verify(reader).readPositiveLong(COURSE_ID);
        verify(view).promptForWriteOperationFlow(CONFIRMATION_PROMPT);
        verify(reader).readRequiredText(CONFIRMATION);
        verify(service).removeStudentFromCourse(studentId, courseId);
        verify(view).showSuccessMessageOnRemovalFromCourse();
    }

    @Test
    void execute_shouldThrowException_whenConfirmationIsInvalid() {
        long studentId = 1L;
        long courseId = 2L;
        String invalidConfirmation = "maybe";

        when(reader.readPositiveLong(STUDENT_ID)).thenReturn(studentId);
        when(reader.readPositiveLong(COURSE_ID)).thenReturn(courseId);
        when(reader.readRequiredText(CONFIRMATION)).thenReturn(invalidConfirmation);

        assertThatExceptionOfType(InvalidConfirmationException.class)
                .isThrownBy(action::execute)
                .withMessage("Confirmation must be 'Y' or 'N', but was 'maybe'.");

        verify(view).promptForWriteOperationFlow("Enter student ID:");
        verify(reader).readPositiveLong(STUDENT_ID);
        verify(view).promptForWriteOperationFlow("Enter course ID:");
        verify(reader).readPositiveLong(COURSE_ID);
        verify(view).promptForWriteOperationFlow(CONFIRMATION_PROMPT);
        verify(reader).readRequiredText(CONFIRMATION);
        verifyNoInteractions(service);
        verify(view, never()).showSuccessMessageOnRemovalFromCourse();
        verify(view, never()).showCancellationMessageOnRemovalFromCourse();
        verifyNoMoreInteractions(view, reader);
    }
}
