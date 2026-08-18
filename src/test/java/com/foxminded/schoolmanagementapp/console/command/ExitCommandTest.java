package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExitCommandTest {

    @Mock
    private ConsoleView view;
    @InjectMocks
    private ExitCommand command;

    @Test
    void menuOption_shouldReturnExitOption() {
        assertThat(command.menuOption()).isEqualTo(MenuOption.EXIT);
    }

    @Test
    void execute_shouldShowGoodbyeAndReturnExit() {
        ExecuteControl actual = command.execute();

        assertThat(actual).isEqualTo(ExecuteControl.EXIT);
        verify(view).showGoodbye();
    }
}
