package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ViewGroupsAction implements MenuActionRunner {

    private final GroupService groupService;
    private final ConsoleView view;

    @Override
    public MenuAction getAction() {
        return MenuAction.VIEW_GROUPS;
    }

    @Override
    public LoopStatus execute() {
        List<Group> groups = groupService.findAll();

        view.showGroups(groups);

        return LoopStatus.CONTINUE;
    }
}