package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ExitCommand implements MenuCommand {

    private final ConsoleView view;

    @Override
    public MenuOption menuOption() {
        return MenuOption.EXIT;
    }

    @Override
    public ExecuteControl execute() {
        view.showGoodbye();

        return ExecuteControl.EXIT;
    }
}