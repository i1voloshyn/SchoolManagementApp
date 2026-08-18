package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuOption;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.service.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ViewCoursesHandler implements MenuOptionHandler {

    private final CourseService courseService;
    private final ConsoleView view;

    @Override
    public MenuOption menuOption() {
        return MenuOption.VIEW_COURSES;
    }

    @Override
    public LoopStatus execute() {
        List<CourseDto> courses = courseService.findAll();

        view.showCourses(courses);

        return LoopStatus.CONTINUE;
    }
}