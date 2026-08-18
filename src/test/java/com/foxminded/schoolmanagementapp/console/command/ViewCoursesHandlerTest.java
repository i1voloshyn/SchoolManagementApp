package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.service.CourseService;
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
class ViewCoursesHandlerTest {

    @Mock
    private CourseService courseService;
    @Mock
    private ConsoleView view;
    @InjectMocks
    private ViewCoursesHandler command;

    @Test
    void menuOption_shouldReturnViewCoursesOption() {
        assertThat(command.menuOption()).isEqualTo(MenuOption.VIEW_COURSES);
    }

    @Test
    void execute_shouldFindAndDisplayCourses() {
        List<CourseDto> courses = List.of(
                new CourseDto(1L, "Java", "Java programming course"),
                new CourseDto(2L, "SQL", "Relational databases course")
        );
        when(courseService.findAll()).thenReturn(courses);

        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(courseService).findAll();
        verify(view).showCourses(courses);
    }
}
