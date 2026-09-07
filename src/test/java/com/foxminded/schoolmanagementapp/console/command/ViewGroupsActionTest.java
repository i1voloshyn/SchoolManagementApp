package com.foxminded.schoolmanagementapp.console.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ViewGroupsActionTest {

    @Mock private GroupService groupService;
    @Mock private ConsoleView view;
    @InjectMocks private ViewGroupsAction command;

    @Test
    void getAction() {
        assertThat(command.getAction()).isEqualTo(MenuAction.VIEW_GROUPS);
    }

    @Test
    void execute_shouldFindAndDisplayGroups() {
        List<Group> groups = List.of(Group.builder().id(1L).name("AA-01").build(), Group.builder().id(2L).name("AA-02").build());
        when(groupService.findAll()).thenReturn(groups);

        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(groupService).findAll();
        verify(view).showGroups(groups);
    }
}
