package com.foxminded.schoolmanagementapp.console;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import com.foxminded.schoolmanagementapp.console.command.MenuActionRunner;
import java.util.List;
import org.junit.jupiter.api.Test;

class MenuActionRunnerDispatcherTest {

    @Test
    void constructor_shouldThrowException_whenMultipleCommandsUseSameOption() {
        MenuActionRunner first = commandFor(MenuAction.VIEW_COURSES);
        MenuActionRunner second = commandFor(MenuAction.VIEW_COURSES);

        assertThatIllegalStateException()
                .isThrownBy(() -> new MenuActionRunnerDispatcher(List.of(first, second)))
                .withMessage("Multiple commands registered for menu option: VIEW_COURSES");
    }

    @Test
    void dispatch_shouldThrowException_whenCommandIsNotRegistered() {
        MenuActionRunner registeredCommand = commandFor(MenuAction.VIEW_GROUPS);

        MenuActionRunnerDispatcher dispatcher =
                new MenuActionRunnerDispatcher(List.of(registeredCommand));

        assertThatIllegalStateException()
                .isThrownBy(() -> dispatcher.dispatch(MenuAction.EXIT))
                .withMessage("No command registered for menu action: EXIT");
    }

    private MenuActionRunner commandFor(MenuAction option) {
        LoopStatus execute = option == MenuAction.EXIT ? LoopStatus.EXIT : LoopStatus.CONTINUE;
        return new MenuActionRunner() {
            @Override
            public MenuAction getAction() {
                return option;
            }

            @Override
            public LoopStatus execute() {
                return execute;
            }
        };
    }
}
