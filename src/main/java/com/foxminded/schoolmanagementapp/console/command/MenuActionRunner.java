package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;

public interface MenuActionRunner {

    MenuAction getAction();

    LoopStatus execute();
}
