package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ViewGroupsHandler implements MenuOptionHandler {

    private final GroupService groupService;
    private final ConsoleView view;

    @Override
    public MenuOption menuOption() {
        return MenuOption.VIEW_GROUPS;
    }

    @Override
    public LoopStatus execute() {
        List<Group> groups = groupService.findAll();

        view.showGroups(groups);

        return LoopStatus.CONTINUE;
    }
}