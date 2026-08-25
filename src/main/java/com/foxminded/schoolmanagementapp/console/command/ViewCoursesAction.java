package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.service.CourseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ViewCoursesAction implements MenuActionRunner {

    private final CourseService courseService;
    private final ConsoleView view;

    @Override
    public MenuAction getAction() {
        return MenuAction.VIEW_COURSES;
    }

    @Override
    public LoopStatus execute() {
        List<CourseDto> courses = courseService.findAll();

        view.showCourses(courses);

        return LoopStatus.CONTINUE;
    }
}
