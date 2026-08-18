package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.command.ExecuteControl;
import com.foxminded.schoolmanagementapp.console.command.MenuCommand;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MenuCommandDispatcher {

    private final Map<MenuOption, MenuCommand> commands;

    public MenuCommandDispatcher(List<MenuCommand> commandList) {
        EnumMap<MenuOption, MenuCommand> commandMap =
                new EnumMap<>(MenuOption.class);

        for (MenuCommand command : commandList) {
            MenuCommand previous = commandMap.put(
                    command.menuOption(),
                    command
            );

            if (previous != null) {
                throw new IllegalStateException(
                        "Multiple commands registered for menu option: "
                                + command.menuOption()
                );
            }
        }

        this.commands = Map.copyOf(commandMap);
    }

    public ExecuteControl dispatch(MenuOption option) {
        MenuCommand command = commands.get(option);
        if (command == null) {
            throw new IllegalStateException(
                    "No command registered for menu option: " + option
            );
        }
        return command.execute();
    }
}