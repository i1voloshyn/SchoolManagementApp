package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class FindStudentsByCourseNameAction implements MenuActionRunner {
    private static final String COURSE_NAME = "Course name";

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

        String courseName = inputReader.readRequiredText(COURSE_NAME);

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
