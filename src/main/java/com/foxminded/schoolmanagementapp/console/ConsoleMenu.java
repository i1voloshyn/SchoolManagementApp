package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.SchoolManagementException;
import com.foxminded.schoolmanagementapp.exception.consoleException.ConsoleInputException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
            return actionRunnerDispatcher.dispatch(action);
        } catch (ConsoleInputException exception) {
            view.showInputError(exception.getMessage());
            return LoopStatus.CONTINUE;
        } catch (SchoolManagementException exception) {
            view.showOperationError(exception.getMessage());
            return LoopStatus.CONTINUE;
        }
    }
}
