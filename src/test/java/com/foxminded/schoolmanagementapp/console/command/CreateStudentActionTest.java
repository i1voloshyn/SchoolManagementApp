package com.foxminded.schoolmanagementapp.console.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.exception.consoleException.BlankConsoleInputException;
import com.foxminded.schoolmanagementapp.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateStudentActionTest {
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";

    @Mock
    StudentService service;
    @Mock
    ConsoleInputReader reader;
    @Mock
    ConsoleView view;
    @InjectMocks
    CreateStudentAction action;

    @Test
    void execute_shouldNotCreateStudent_whenFirstNameValidationFails() {
        var exception = new BlankConsoleInputException(FIRST_NAME);

        when(reader.readRequiredText(FIRST_NAME)).thenThrow(exception);

        assertThatExceptionOfType(BlankConsoleInputException.class)
                .isThrownBy(action::execute)
                .isSameAs(exception);

        verify(view).promptForWriteOperationFlow("Enter student first name:");
        verify(reader).readRequiredText(FIRST_NAME);
        verifyNoInteractions(service);
        verify(view, never()).showSuccessMessageOnCreation(anyString());
        verifyNoMoreInteractions(view, reader);
    }

    @Test
    void execute_shouldCreateStudent() {
        String firstName = "Joe";
        String lastName = "Nevada";
        StudentDto dtoToSave = new StudentDto(null, null, firstName, lastName);
        StudentDto created = new StudentDto(1L, 2L, firstName, lastName);

        when(reader.readRequiredText(FIRST_NAME)).thenReturn(firstName);
        when(reader.readRequiredText(LAST_NAME)).thenReturn(lastName);
        when(service.addStudent(dtoToSave)).thenReturn(created);

        LoopStatus actual = action.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(view).promptForWriteOperationFlow("Enter student first name:");
        verify(reader).readRequiredText(FIRST_NAME);
        verify(view).promptForWriteOperationFlow("Enter student last name:");
        verify(reader).readRequiredText(LAST_NAME);
        verify(service).addStudent(dtoToSave);
        verify(view).showSuccessMessageOnCreation("Student");
    }
}
