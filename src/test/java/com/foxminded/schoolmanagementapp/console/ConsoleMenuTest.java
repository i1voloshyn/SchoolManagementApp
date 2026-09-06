package com.foxminded.schoolmanagementapp.console;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonNumericInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsoleMenuTest {

    @Mock ConsoleInputReader reader;
    @Mock ConsoleView view;
    @Mock MenuActionRunnerDispatcher dispatcher;
    @InjectMocks ConsoleMenu menu;

    @Test
    void start_shouldDispatchSelectedActionAndStop_whenActionReturnsExit() {
        when(reader.readActionNumber()).thenReturn(MenuAction.EXIT.number());
        when(dispatcher.dispatch(MenuAction.EXIT)).thenReturn(LoopStatus.EXIT);

        menu.start();

        verify(view).showGreeting();
        verify(view).showMenu();
        verify(reader).readActionNumber();
        verify(dispatcher).dispatch(MenuAction.EXIT);
        verifyNoMoreInteractions(view, reader, dispatcher);
    }

    @Test
    void start_shouldShowInputErrorAndContinue_whenInputIsNotNumeric() {
        var exception = new NonNumericInputException("Menu item", "abc");

        when(reader.readActionNumber()).thenThrow(exception).thenReturn(MenuAction.EXIT.number());
        when(dispatcher.dispatch(MenuAction.EXIT)).thenReturn(LoopStatus.EXIT);

        menu.start();

        verify(view).showGreeting();
        verify(view, times(2)).showMenu();
        verify(reader, times(2)).readActionNumber();
        verify(view).showInputError(exception.getMessage());
        verify(dispatcher).dispatch(MenuAction.EXIT);
        verifyNoMoreInteractions(view, reader, dispatcher);
    }

    @Test
    void start_shouldShowOperationErrorAndContinue_whenStudentDoesNotExist() {
        long studentId = 999L;
        var exception = new StudentNotFoundException(studentId);

        when(reader.readActionNumber())
                .thenReturn(MenuAction.DELETE_STUDENT.number(), MenuAction.EXIT.number());
        when(dispatcher.dispatch(MenuAction.DELETE_STUDENT)).thenThrow(exception);
        when(dispatcher.dispatch(MenuAction.EXIT)).thenReturn(LoopStatus.EXIT);

        menu.start();

        verify(view).showGreeting();
        verify(view, times(2)).showMenu();
        verify(reader, times(2)).readActionNumber();
        verify(dispatcher).dispatch(MenuAction.DELETE_STUDENT);
        verify(view).showOperationError("Student not found with ID: " + studentId);
        verify(dispatcher).dispatch(MenuAction.EXIT);
        verifyNoMoreInteractions(view, reader, dispatcher);
    }
}
