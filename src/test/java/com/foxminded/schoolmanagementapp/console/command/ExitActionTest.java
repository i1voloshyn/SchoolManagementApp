package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExitActionTest {

    @Mock
    private ConsoleView view;
    @InjectMocks
    private ExitAction command;

    @Test
    void getAction() {
        assertThat(command.getAction()).isEqualTo(MenuAction.EXIT);
    }

    @Test
    void execute_shouldShowGoodbyeAndReturnExit() {
        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.EXIT);
        verify(view).showGoodbye();
    }
}
