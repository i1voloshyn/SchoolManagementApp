package com.foxminded.schoolmanagementapp.console.command;

import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.MAXIMUM_STUDENT_COUNT;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FindGroupsByMaximumStudentCountAction implements MenuActionRunner {
    private final ConsoleView view;
    private final ConsoleInputReader inputReader;
    private final GroupService groupService;

    @Override
    public MenuAction getAction() {
        return MenuAction.FIND_GROUPS_BY_MAX_STUDENT_COUNT;
    }

    @Override
    public LoopStatus execute() {
        view.promptForMaximumStudentCount();

        int maximumStudentCount = inputReader.readNonNegativeInteger(MAXIMUM_STUDENT_COUNT.text());

        List<Group> groups = groupService.findByMaximumStudentCount(maximumStudentCount);

        view.showGroups(groups);

        return LoopStatus.CONTINUE;
    }
}
