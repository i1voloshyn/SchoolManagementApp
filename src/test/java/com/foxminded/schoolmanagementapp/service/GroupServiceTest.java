package com.foxminded.schoolmanagementapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.repository.GroupRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock private GroupRepository groupRepository;
    @InjectMocks private GroupService service;

    @Test
    void createGroup_shouldSaveAndReturnGroup() {
        Group groupToCreate = Group.builder().name("AA-01").build();
        Group savedGroup = Group.builder().id(1L).name("AA-01").build();

        when(groupRepository.save(groupToCreate)).thenReturn(savedGroup);

        Group actual = service.createGroup(groupToCreate);

        assertThat(actual).isEqualTo(savedGroup);
        verify(groupRepository).save(groupToCreate);
    }

    @Test
    void createGroup_shouldRejectNullGroup() {
        assertThatIllegalArgumentException().isThrownBy(() -> service.createGroup(null));
        verifyNoInteractions(groupRepository);
    }

    @Test
    void createGroup_shouldRejectGroupWithId() {
        Group existingGroup = Group.builder().id(1L).name("AA-01").build();

        assertThatIllegalArgumentException().isThrownBy(() -> service.createGroup(existingGroup));
        verifyNoInteractions(groupRepository);
    }

    @Test
    void createGroup_shouldRejectInvalidName() {
        Group invalidGroup = Group.builder().id(null).name("Group A").build();

        assertThatIllegalArgumentException().isThrownBy(() -> service.createGroup(invalidGroup));
        verifyNoInteractions(groupRepository);
    }

    @Test
    void findGroupsByMaximumStudentCount_shouldReturnRepositoryResult() {
        List<Group> expected = List.of(Group.builder().id(1L).name("AA-01").build(), Group.builder().id(2L).name("AA-02").build());
        when(groupRepository.findByMaximumStudentCount(10)).thenReturn(expected);

        List<Group> actual = service.findByMaximumStudentCount(10);

        assertThat(actual).containsExactlyElementsOf(expected);
        verify(groupRepository).findByMaximumStudentCount(10);
    }

    @Test
    void findGroupsByMaximumStudentCount_shouldRejectNegativeCount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.findByMaximumStudentCount(-1));
        verifyNoInteractions(groupRepository);
    }
}
