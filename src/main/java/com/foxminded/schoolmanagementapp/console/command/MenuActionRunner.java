package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;

public interface MenuActionRunner {

    MenuAction getAction();

    LoopStatus execute();
}
