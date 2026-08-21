package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewGroupsActionTest {

    @Mock
    private GroupService groupService;
    @Mock
    private ConsoleView view;
    @InjectMocks
    private ViewGroupsAction command;

    @Test
    void getAction() {
        assertThat(command.getAction()).isEqualTo(MenuAction.VIEW_GROUPS);
    }

    @Test
    void execute_shouldFindAndDisplayGroups() {
        List<Group> groups = List.of(
                new Group(1L, "AA-01"),
                new Group(2L, "AA-02")
        );
        when(groupService.findAll()).thenReturn(groups);

        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(groupService).findAll();
        verify(view).showGroups(groups);
    }
}
