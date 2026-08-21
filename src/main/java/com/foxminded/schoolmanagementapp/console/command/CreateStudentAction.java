package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CreateStudentAction implements MenuActionRunner {
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String FIRST_NAME_PROMPT = "Enter student first name:";
    private static final String LAST_NAME_PROMPT = "Enter student last name:";

    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final StudentService studentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.CREATE_STUDENT;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(FIRST_NAME_PROMPT);
        String firstName = inputReader.readRequiredText(FIRST_NAME);

        consoleView.promptForWriteOperationFlow(LAST_NAME_PROMPT);
        String lastName = inputReader.readRequiredText(LAST_NAME);

        studentService.addStudent(new StudentDto(null, null, firstName, lastName));

        consoleView.showSuccessMessageOnCreation("Student");
        return LoopStatus.CONTINUE;
    }
}
