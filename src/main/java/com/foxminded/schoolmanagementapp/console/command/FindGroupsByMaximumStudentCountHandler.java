package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class FindGroupsByMaximumStudentCountHandler
        implements MenuOptionHandler {

    private static final String MAXIMUM_STUDENT_COUNT = "Maximum student count";

    private final ConsoleInputReader inputReader;
    private final ConsoleView view;
    private final GroupService groupService;

    @Override
    public MenuOption menuOption() {
        return MenuOption.FIND_GROUPS_BY_MAX_STUDENT_COUNT;
    }

    @Override
    public LoopStatus execute() {
        view.promptForMaximumStudentCount();

        int maximumStudentCount =
                inputReader.readNonNegativeInteger(
                        MAXIMUM_STUDENT_COUNT
                );

        List<Group> groups =
                groupService.findByMaximumStudentCount(
                        maximumStudentCount
                );

        view.showGroups(groups);

        return LoopStatus.CONTINUE;
    }
}