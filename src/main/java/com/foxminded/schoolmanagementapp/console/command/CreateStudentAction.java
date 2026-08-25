package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.Entity;
import com.foxminded.schoolmanagementapp.console.constants.Field;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.constants.Prompt;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.service.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Student creation requested...");
        consoleView.promptForWriteOperationFlow(Prompt.FIRST_NAME.getValue());
        String firstName = inputReader.readRequiredText(Field.FIRST_NAME.getValue());

        consoleView.promptForWriteOperationFlow(Prompt.LAST_NAME.getValue());
        String lastName = inputReader.readRequiredText(Field.LAST_NAME.getValue());
        log.debug("Student creation input accepted");

        StudentDto student = studentService.addStudent(new StudentDto(null, null, firstName, lastName));

        log.info("Student creation completed: studentId={}", student.id());

        consoleView.showSuccessMessageOnCreation(Entity.STUDENT.getValue());
        return LoopStatus.CONTINUE;
    }
}
