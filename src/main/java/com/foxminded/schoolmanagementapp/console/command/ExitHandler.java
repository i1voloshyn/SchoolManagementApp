package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ExitHandler implements MenuOptionHandler {

    private final ConsoleView view;

    @Override
    public MenuOption menuOption() {
        return MenuOption.EXIT;
    }

    @Override
    public LoopStatus execute() {
        view.showGoodbye();

        return LoopStatus.EXIT;
    }
}