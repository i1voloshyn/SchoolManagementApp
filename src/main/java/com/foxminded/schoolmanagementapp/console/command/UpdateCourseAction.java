package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.Entity;
import com.foxminded.schoolmanagementapp.console.constants.Field;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.constants.Prompt;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.service.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UpdateCourseAction implements MenuActionRunner {
    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final CourseService courseService;

    @Override
    public MenuAction getAction() {
        return MenuAction.UPDATE_COURSE;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(Prompt.COURSE_ID.getValue());
        long courseId = inputReader.readPositiveLong(Field.COURSE_ID.getValue());

        consoleView.promptForWriteOperationFlow(Prompt.COURSE_NAME.getValue());
        String courseName = inputReader.readRequiredText(Field.COURSE_NAME.getValue());

        consoleView.promptForWriteOperationFlow(Prompt.COURSE_DESCRIPTION.getValue());
        String courseDescription =
                inputReader.readRequiredText(Field.COURSE_DESCRIPTION.getValue());

        courseService.updateCourse(
                new CourseDto(courseId, courseName, courseDescription));
        consoleView.showSuccessMessageOnUpdate(Entity.COURSE.getValue());

        return LoopStatus.CONTINUE;
    }
}
