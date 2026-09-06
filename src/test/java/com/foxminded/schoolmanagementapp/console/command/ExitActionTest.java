package com.foxminded.schoolmanagementapp.console.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExitActionTest {

    @Mock private ConsoleView view;
    @InjectMocks private ExitAction command;

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
