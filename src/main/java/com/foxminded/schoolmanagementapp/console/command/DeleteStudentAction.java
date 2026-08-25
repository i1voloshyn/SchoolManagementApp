package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.constants.ConfirmationOption;
import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.Entity;
import com.foxminded.schoolmanagementapp.console.constants.Field;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.constants.Prompt;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.InvalidConfirmationException;
import com.foxminded.schoolmanagementapp.service.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class DeleteStudentAction implements MenuActionRunner {
    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final StudentService studentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.DELETE_STUDENT;
    }

    @Override
    public LoopStatus execute() {
        log.info("Request to delete a student...");

        consoleView.promptForWriteOperationFlow(Prompt.STUDENT_ID.getValue());
        long studentId = inputReader.readPositiveLong(Field.STUDENT_ID.getValue());

        consoleView.promptForWriteOperationFlow(confirmationPrompt());
        String confirmationAnswer = inputReader.readRequiredText(Field.CONFIRMATION.getValue());

        if (isRemovalConfirmed(confirmationAnswer)) {
            studentService.deleteStudent(studentId);
            consoleView.showSuccessMessageOnRemoval(Entity.STUDENT.getValue());
        } else {
            log.info("Cancel student deletion");
            consoleView.showCancellationMessageOnRemoval(Entity.STUDENT.getValue());
        }
        log.info("Successfully deleted student with ID: {}", studentId);
        return LoopStatus.CONTINUE;
    }

    private String confirmationPrompt() {
        return Prompt.DELETE_STUDENT_CONFIRMATION
                .getValue()
                .formatted(
                        ConfirmationOption.DELETE.getValue(),
                        ConfirmationOption.CANCEL.getValue());
    }

    private boolean isRemovalConfirmed(String answer) {
        if (ConfirmationOption.DELETE.getValue().equalsIgnoreCase(answer)) {
            return true;
        }
        if (ConfirmationOption.CANCEL.getValue().equalsIgnoreCase(answer)) {
            return false;
        }

        throw new InvalidConfirmationException(
                answer,
                ConfirmationOption.DELETE.getValue(),
                ConfirmationOption.CANCEL.getValue());
    }
}
