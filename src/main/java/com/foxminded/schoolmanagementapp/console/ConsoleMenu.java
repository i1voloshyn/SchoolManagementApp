package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.ConsoleInputException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConsoleMenu {

    private final ConsoleInputReader inputReader;
    private final ConsoleView view;
    private final MenuCommandDispatcher commandDispatcher;

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
            MenuOption option = inputReader.readMenuOption();
            return commandDispatcher.dispatch(option);
        } catch (ConsoleInputException exception) {
            view.showInputError(exception.getMessage());
            return LoopStatus.CONTINUE;
        }
    }
}