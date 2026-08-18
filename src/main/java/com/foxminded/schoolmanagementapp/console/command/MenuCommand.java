package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.MenuOption;

public interface MenuCommand {

    MenuOption menuOption();

    ExecuteControl execute();
}