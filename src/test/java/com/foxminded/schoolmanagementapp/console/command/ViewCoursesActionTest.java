package com.foxminded.schoolmanagementapp.console.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.service.CourseService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ViewCoursesActionTest {

    @Mock private CourseService courseService;
    @Mock private ConsoleView view;
    @InjectMocks private ViewCoursesAction command;

    @Test
    void getAction() {
        assertThat(command.getAction()).isEqualTo(MenuAction.VIEW_COURSES);
    }

    @Test
    void execute_shouldFindAndDisplayCourses() {
        List<CourseDto> courses =
                List.of(
                        new CourseDto(1L, "Java", "Java programming course"),
                        new CourseDto(2L, "SQL", "Relational databases course"));
        when(courseService.findAll()).thenReturn(courses);

        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(courseService).findAll();
        verify(view).showCourses(courses);
    }
}
