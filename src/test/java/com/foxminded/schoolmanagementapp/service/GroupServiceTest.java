package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private GroupService service;

    @Test
    void findGroupsByMaximumStudentCount_shouldReturnRepositoryResult() {
        List<Group> expected = List.of(
                new Group(1L, "AA-01"),
                new Group(2L, "BB-02")
        );
        when(groupRepository.findByMaximumStudentCount(10))
                .thenReturn(expected);

        List<Group> actual =
                service.findByMaximumStudentCount(10);

        assertThat(actual).containsExactlyElementsOf(expected);
        verify(groupRepository).findByMaximumStudentCount(10);
    }

    @Test
    void findGroupsByMaximumStudentCount_shouldRejectNegativeCount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        service.findByMaximumStudentCount(-1));
        verifyNoInteractions(groupRepository);
    }

}