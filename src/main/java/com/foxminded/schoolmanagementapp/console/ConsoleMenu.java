package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.SchoolManagementException;
import com.foxminded.schoolmanagementapp.exception.consoleException.ConsoleInputException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class ConsoleMenu {
    private final ConsoleView view;
    private final ConsoleInputReader inputReader;
    private final MenuActionRunnerDispatcher actionRunnerDispatcher;

    public void start() {
        view.showGreeting();

        LoopStatus status = LoopStatus.CONTINUE;

        while (status == LoopStatus.CONTINUE) {
            status = processNextCommand();
        }
    }

    private LoopStatus processNextCommand() {
        view.showMenu();
        try {
            int actionNumber = inputReader.readActionNumber();
            MenuAction action = MenuAction.fromNumber(actionNumber);
            log.info("Menu action selected: number={}, action={}", actionNumber, action);
            return actionRunnerDispatcher.dispatch(action);
        } catch (ConsoleInputException exception) {
            log.warn(
                    "Console input rejected: type={}, message={}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage());
            view.showInputError(exception.getMessage());
            return LoopStatus.CONTINUE;
        } catch (SchoolManagementException exception) {
            log.warn(
                    "Console operation failed: type={}, message={}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage());
            view.showOperationError(exception.getMessage());
            return LoopStatus.CONTINUE;
        }
    }

}
