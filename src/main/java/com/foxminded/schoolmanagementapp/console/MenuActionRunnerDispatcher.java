package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.command.MenuActionRunner;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import org.springframework.stereotype.Component;

@Component
public class MenuActionRunnerDispatcher {
    private final Map<MenuAction, MenuActionRunner> actions;

    public MenuActionRunnerDispatcher(List<MenuActionRunner> actionList) {
        EnumMap<MenuAction, MenuActionRunner> actionsMap = new EnumMap<>(MenuAction.class);

        for (MenuActionRunner actionRunner : actionList) {
            MenuActionRunner previous = actionsMap.put(actionRunner.getAction(), actionRunner);

            if (previous != null) {
                throw new IllegalStateException(
                        "Multiple commands registered for menu option: "
                                + actionRunner.getAction());
            }
        }

        this.actions = Map.copyOf(actionsMap);
    }

    public LoopStatus dispatch(MenuAction action) {
        MenuActionRunner actionRunner = actions.get(action);
        if (actionRunner == null) {
            throw new IllegalStateException("No command registered for menu action: " + action);
        }
        return actionRunner.execute();
    }
}
