package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExitHandlerTest {

    @Mock
    private ConsoleView view;
    @InjectMocks
    private ExitHandler command;

    @Test
    void menuOption_shouldReturnExitOption() {
        assertThat(command.menuOption()).isEqualTo(MenuOption.EXIT);
    }

    @Test
    void execute_shouldShowGoodbyeAndReturnExit() {
        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.EXIT);
        verify(view).showGoodbye();
    }
}
