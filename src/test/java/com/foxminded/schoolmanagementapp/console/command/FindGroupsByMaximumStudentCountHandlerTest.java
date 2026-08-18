package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.NegativeNumberException;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.service.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindGroupsByMaximumStudentCountHandlerTest {

    private static final String FIELD_NAME = "Maximum student count";

    @Mock
    private ConsoleInputReader inputReader;
    @Mock
    private ConsoleView view;
    @Mock
    private GroupService groupService;
    @InjectMocks
    private FindGroupsByMaximumStudentCountHandler command;

    @Test
    void menuOption_shouldReturnFindGroupsOption() {
        assertThat(command.menuOption())
                .isEqualTo(MenuOption.FIND_GROUPS_BY_MAX_STUDENT_COUNT);
    }

    @Test
    void execute_shouldReadMaximumCountAndDisplayMatchingGroups() {
        int maximumStudentCount = 10;
        List<Group> groups = List.of(
                new Group(1L, "AA-01"),
                new Group(2L, "AA-02")
        );
        when(inputReader.readNonNegativeInteger(FIELD_NAME))
                .thenReturn(maximumStudentCount);
        when(groupService.findByMaximumStudentCount(maximumStudentCount))
                .thenReturn(groups);

        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(view).promptForMaximumStudentCount();
        verify(inputReader).readNonNegativeInteger(FIELD_NAME);
        verify(groupService).findByMaximumStudentCount(maximumStudentCount);
        verify(view).showGroups(groups);
    }

    @Test
    void execute_shouldNotCallService_whenInputValidationFails() {
        NegativeNumberException exception =
                new NegativeNumberException(FIELD_NAME, -1);
        when(inputReader.readNonNegativeInteger(FIELD_NAME))
                .thenThrow(exception);

        assertThatExceptionOfType(NegativeNumberException.class)
                .isThrownBy(command::execute)
                .isSameAs(exception);

        verify(view).promptForMaximumStudentCount();
        verifyNoInteractions(groupService);
        verify(view, never()).showGroups(anyList());
    }
}
