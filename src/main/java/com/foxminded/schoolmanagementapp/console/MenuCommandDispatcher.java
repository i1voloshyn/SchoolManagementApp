package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.command.MenuOptionHandler;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MenuCommandDispatcher {

    private final Map<MenuOption, MenuOptionHandler> commands;

    public MenuCommandDispatcher(List<MenuOptionHandler> commandList) {
        EnumMap<MenuOption, MenuOptionHandler> commandMap =
                new EnumMap<>(MenuOption.class);

        for (MenuOptionHandler command : commandList) {
            MenuOptionHandler previous = commandMap.put(
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

    public LoopStatus dispatch(MenuOption option) {
        MenuOptionHandler command = commands.get(option);
        if (command == null) {
            throw new IllegalStateException(
                    "No command registered for menu option: " + option
            );
        }
        return command.execute();
    }
}