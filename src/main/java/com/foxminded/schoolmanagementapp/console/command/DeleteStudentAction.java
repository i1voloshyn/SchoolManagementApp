package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.InvalidConfirmationException;
import com.foxminded.schoolmanagementapp.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DeleteStudentAction implements MenuActionRunner {
    private static final String STUDENT_ID_PROMPT = "Enter student ID:";
    private static final String STUDENT_ID = "Student ID";
    private static final String CONFIRMATION = "Confirmation";
    private static final String DELETE = "Y";
    private static final String CANCEL = "N";
    private static final String CONFIRMATION_PROMPT =
            "Permanently delete student? Type '%s' to delete, '%s' to cancel"
                    .formatted(DELETE, CANCEL);

    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final StudentService studentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.DELETE_STUDENT;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(STUDENT_ID_PROMPT);
        long studentId = inputReader.readPositiveLong(STUDENT_ID);

        consoleView.promptForWriteOperationFlow(CONFIRMATION_PROMPT);
        String confirmationAnswer = inputReader.readRequiredText(CONFIRMATION);

        if (isRemovalConfirmed(confirmationAnswer)) {
            studentService.deleteStudent(studentId);
            consoleView.showSuccessMessageOnRemoval("Student");
        } else {
            consoleView.showCancellationMessageOnRemoval("Student");
        }
        return LoopStatus.CONTINUE;
    }

    private boolean isRemovalConfirmed(String answer) {
        if (DELETE.equalsIgnoreCase(answer)) {
            return true;
        }
        if (CANCEL.equalsIgnoreCase(answer)) {
            return false;
        }

        throw new InvalidConfirmationException(answer, DELETE, CANCEL);
    }
}
