package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.command.ExecuteControl;
import com.foxminded.schoolmanagementapp.console.command.MenuCommand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class MenuCommandDispatcherTest {

    @Test
    void constructor_shouldThrowException_whenMultipleCommandsUseSameOption() {
        MenuCommand first = commandFor(MenuOption.VIEW_COURSES);
        MenuCommand second = commandFor(MenuOption.VIEW_COURSES);

        assertThatIllegalStateException()
                .isThrownBy(() -> new MenuCommandDispatcher(
                        List.of(first, second)
                ))
                .withMessage(
                        "Multiple commands registered for menu option: VIEW_COURSES"
                );

    }

    @Test
    void dispatch_shouldThrowException_whenCommandIsNotRegistered() {
        MenuCommand registeredCommand = commandFor(MenuOption.VIEW_GROUPS);

        MenuCommandDispatcher dispatcher = new MenuCommandDispatcher(
                List.of(registeredCommand)
        );

        assertThatIllegalStateException()
                .isThrownBy(() -> dispatcher.dispatch(MenuOption.EXIT))
                .withMessage(
                        "No command registered for menu option: EXIT"
                );

    }

    private MenuCommand commandFor(MenuOption option) {
        ExecuteControl execute = option == MenuOption.EXIT
                ? ExecuteControl.EXIT : ExecuteControl.CONTINUE;
        return new MenuCommand() {
            @Override
            public MenuOption menuOption() {
                return option;
            }

            @Override
            public ExecuteControl execute() {
                return execute;
            }
        };
    }


}
