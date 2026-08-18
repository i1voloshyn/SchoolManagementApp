package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ViewGroupsCommand implements MenuCommand {

    private final GroupService groupService;
    private final ConsoleView view;

    @Override
    public MenuOption menuOption() {
        return MenuOption.VIEW_GROUPS;
    }

    @Override
    public ExecuteControl execute() {
        List<Group> groups = groupService.findAll();

        view.showGroups(groups);

        return ExecuteControl.CONTINUE;
    }
}