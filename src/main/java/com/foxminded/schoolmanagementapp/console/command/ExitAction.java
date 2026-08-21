package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ExitAction implements MenuActionRunner {

    private final ConsoleView view;

    @Override
    public MenuAction getAction() {
        return MenuAction.EXIT;
    }

    @Override
    public LoopStatus execute() {
        view.showGoodbye();

        return LoopStatus.EXIT;
    }
}
