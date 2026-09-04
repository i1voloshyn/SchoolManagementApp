package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
