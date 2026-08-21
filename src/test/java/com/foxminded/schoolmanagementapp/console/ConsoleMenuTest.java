package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsoleMenuTest {

    @Mock
    ConsoleInputReader reader;
    @Mock
    ConsoleView view;
    @Mock
    MenuActionRunnerDispatcher dispatcher;
    @InjectMocks
    ConsoleMenu menu;

    @Test
    void start_shouldShowOperationErrorAndContinue_whenStudentDoesNotExist() {
        long studentId = 999L;
        var exception = new StudentNotFoundException(studentId);

        when(reader.readActionNumber()).thenReturn(
                MenuAction.DELETE_STUDENT.number(),
                MenuAction.EXIT.number()
        );
        when(dispatcher.dispatch(MenuAction.DELETE_STUDENT))
                .thenThrow(exception);
        when(dispatcher.dispatch(MenuAction.EXIT)).thenReturn(LoopStatus.EXIT);//mock second action to exit the loop in test

        menu.start();

        verify(view).showGreeting();
        verify(view, times(2)).showMenu();
        verify(dispatcher).dispatch(MenuAction.DELETE_STUDENT);
        verify(view).showOperationError(
                "Student not found with ID: " + studentId
        );
        verify(dispatcher).dispatch(MenuAction.EXIT);
    }
}
