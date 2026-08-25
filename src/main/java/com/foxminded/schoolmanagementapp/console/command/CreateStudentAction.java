package com.foxminded.schoolmanagementapp.console.command;

import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.FIRST_NAME;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.FIRST_NAME_PROMPT;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.LAST_NAME;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.LAST_NAME_PROMPT;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.STUDENT;

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
    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final StudentService studentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.CREATE_STUDENT;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(FIRST_NAME_PROMPT.text());
        String firstName = inputReader.readRequiredText(FIRST_NAME.text());

        consoleView.promptForWriteOperationFlow(LAST_NAME_PROMPT.text());
        String lastName = inputReader.readRequiredText(LAST_NAME.text());

        studentService.addStudent(new StudentDto(null, null, firstName, lastName));

        consoleView.showSuccessMessageOnCreation(STUDENT.text());
        return LoopStatus.CONTINUE;
    }
}
