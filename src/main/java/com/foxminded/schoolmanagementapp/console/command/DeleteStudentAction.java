package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DeleteStudentAction implements MenuActionRunner {

    private static final String STUDENT_ID = "Student ID";
    private static final String STUDENT_ID_PROMPT = "Enter student ID:";

    private final ConsoleInputReader inputReader;
    private final StudentService studentService;
    private final ConsoleView consoleView;

    @Override
    public MenuAction getAction() {
        return MenuAction.DELETE_STUDENT;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(STUDENT_ID_PROMPT);
        long studentId = inputReader.readPositiveLong(STUDENT_ID);

        studentService.deleteStudent(studentId);

        consoleView.showSuccessMessageOnDeletion("Student");
        return LoopStatus.CONTINUE;
    }
}
