package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.command.MenuOptionHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class MenuOptionHandlerDispatcherTest {

    @Test
    void constructor_shouldThrowException_whenMultipleCommandsUseSameOption() {
        MenuOptionHandler first = commandFor(MenuOption.VIEW_COURSES);
        MenuOptionHandler second = commandFor(MenuOption.VIEW_COURSES);

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
        MenuOptionHandler registeredCommand = commandFor(MenuOption.VIEW_GROUPS);

        MenuCommandDispatcher dispatcher = new MenuCommandDispatcher(
                List.of(registeredCommand)
        );

        assertThatIllegalStateException()
                .isThrownBy(() -> dispatcher.dispatch(MenuOption.EXIT))
                .withMessage(
                        "No command registered for menu option: EXIT"
                );

    }

    private MenuOptionHandler commandFor(MenuOption option) {
        LoopStatus execute = option == MenuOption.EXIT
                ? LoopStatus.EXIT : LoopStatus.CONTINUE;
        return new MenuOptionHandler() {
            @Override
            public MenuOption menuOption() {
                return option;
            }

            @Override
            public LoopStatus execute() {
                return execute;
            }
        };
    }


}
