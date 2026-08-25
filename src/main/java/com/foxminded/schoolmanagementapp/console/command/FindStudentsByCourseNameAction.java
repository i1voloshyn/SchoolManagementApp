package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.Field;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.service.StudentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FindStudentsByCourseNameAction implements MenuActionRunner {

    private final ConsoleView view;
    private final ConsoleInputReader inputReader;
    private final StudentService studentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.FIND_STUDENTS_BY_COURSE_NAME;
    }

    @Override
    public LoopStatus execute() {
        view.promptForCourseName();

        String courseName = inputReader.readRequiredText(Field.COURSE_NAME.getValue());

        List<StudentDto> students = findStudents(courseName);

        view.showStudents(courseName, students);

        return LoopStatus.CONTINUE;
    }

    private List<StudentDto> findStudents(String courseName) {
        try {
            return studentService.findStudentsByCourseName(courseName);
        } catch (CourseNotFoundException exception) {
            return List.of();
        }
    }
}
