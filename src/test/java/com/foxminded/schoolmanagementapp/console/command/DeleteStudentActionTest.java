package com.foxminded.schoolmanagementapp.console.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonPositiveNumberException;
import com.foxminded.schoolmanagementapp.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteStudentActionTest {
    private static final String STUDENT_ID = "Student ID";
    private static final String CONFIRMATION = "Confirmation";
    private static final String DELETE = "Y";
    private static final String CANCEL = "N";
    private static final String CONFIRMATION_PROMPT =
            "Permanently delete student? Type '%s' to delete, '%s' to cancel"
                    .formatted(DELETE, CANCEL);

    @Mock
    StudentService service;
    @Mock
    ConsoleInputReader reader;
    @Mock
    ConsoleView view;
    @InjectMocks
    DeleteStudentAction action;

    @Test
    void getAction_shouldReturnDeleteStudent() {
        assertThat(action.getAction()).isEqualTo(MenuAction.DELETE_STUDENT);
    }

    @Nested
    @DisplayName("execute()")
    class ExecuteTests {

        @Nested
        @DisplayName("when student ID validation fails")
        class InvalidStudentId {

            @Test
            void shouldPropagateExceptionAndNotDeleteStudent() {
                var exception = new NonPositiveNumberException(STUDENT_ID, 0);

                when(reader.readPositiveLong(STUDENT_ID)).thenThrow(exception);

                assertThatExceptionOfType(NonPositiveNumberException.class)
                        .isThrownBy(action::execute)
                        .isSameAs(exception);

                verify(view).promptForWriteOperationFlow("Enter student ID:");
                verify(reader).readPositiveLong(STUDENT_ID);
                verifyNoInteractions(service);
                verify(view, never()).showSuccessMessageOnRemoval(anyString());
                verifyNoMoreInteractions(view, reader);
            }
        }

        @Nested
        @DisplayName("when deletion is confirmed")
        class DeletionConfirmed {

            @Test
            void shouldDeleteStudent() {
                long studentId = 1L;

                when(reader.readPositiveLong(STUDENT_ID)).thenReturn(studentId);
                when(reader.readRequiredText(CONFIRMATION)).thenReturn(DELETE);

                LoopStatus actual = action.execute();

                assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
                verify(view).promptForWriteOperationFlow("Enter student ID:");
                verify(reader).readPositiveLong(STUDENT_ID);
                verify(view).promptForWriteOperationFlow(CONFIRMATION_PROMPT);
                verify(reader).readRequiredText(CONFIRMATION);
                verify(service).deleteStudent(studentId);
                verify(view).showSuccessMessageOnRemoval("Student");
            }

            @Test
            void shouldPropagateExceptionAndNotShowSuccess_whenStudentDoesNotExist() {
                long studentId = 999L;
                var exception = new StudentNotFoundException(studentId);

                when(reader.readPositiveLong(STUDENT_ID)).thenReturn(studentId);
                when(reader.readRequiredText(CONFIRMATION)).thenReturn(DELETE);
                doThrow(exception).when(service).deleteStudent(studentId);

                assertThatExceptionOfType(StudentNotFoundException.class)
                        .isThrownBy(action::execute)
                        .isSameAs(exception);

                verify(view).promptForWriteOperationFlow("Enter student ID:");
                verify(reader).readPositiveLong(STUDENT_ID);
                verify(view).promptForWriteOperationFlow(CONFIRMATION_PROMPT);
                verify(reader).readRequiredText(CONFIRMATION);
                verify(service).deleteStudent(studentId);
                verify(view, never()).showSuccessMessageOnRemoval(anyString());
                verifyNoMoreInteractions(view, reader);
            }

        }
    }
}
