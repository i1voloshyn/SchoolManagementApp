package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Console exit requested...");
        view.showGoodbye();
        log.info("Console exit completed");

        return LoopStatus.EXIT;
    }
}
