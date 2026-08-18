package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;

public interface MenuOptionHandler {

    MenuOption menuOption();

    LoopStatus execute();
}