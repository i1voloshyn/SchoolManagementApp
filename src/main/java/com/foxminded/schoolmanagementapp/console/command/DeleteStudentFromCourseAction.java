package com.foxminded.schoolmanagementapp.console.command;

import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.CANCEL_OPTION;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.CONFIRMATION;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.COURSE_ID;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.COURSE_ID_PROMPT;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.DELETE_OPTION;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.DELETE_STUDENT_FROM_COURSE_CONFIRMATION_PROMPT;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.STUDENT_ID;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.STUDENT_ID_PROMPT;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.InvalidConfirmationException;
import com.foxminded.schoolmanagementapp.service.EnrollmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DeleteStudentFromCourseAction implements MenuActionRunner {
    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final EnrollmentService enrollmentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.DELETE_STUDENT_FROM_COURSE;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(STUDENT_ID_PROMPT.text());
        long studentId = inputReader.readPositiveLong(STUDENT_ID.text());

        consoleView.promptForWriteOperationFlow(COURSE_ID_PROMPT.text());
        long courseId = inputReader.readPositiveLong(COURSE_ID.text());

        consoleView.promptForWriteOperationFlow(confirmationPrompt());
        String confirmationAnswer = inputReader.readRequiredText(CONFIRMATION.text());

        if (isRemovalConfirmed(confirmationAnswer)) {
            enrollmentService.removeStudentFromCourse(studentId, courseId);
            consoleView.showSuccessMessageOnRemovalFromCourse();
        } else {
            consoleView.showCancellationMessageOnRemovalFromCourse();
        }

        return LoopStatus.CONTINUE;
    }

    private String confirmationPrompt() {
        return DELETE_STUDENT_FROM_COURSE_CONFIRMATION_PROMPT.format(DELETE_OPTION.text(), CANCEL_OPTION.text());
    }

    private boolean isRemovalConfirmed(String answer) {
        if (DELETE_OPTION.text().equalsIgnoreCase(answer)) {
            return true;
        }
        if (CANCEL_OPTION.text().equalsIgnoreCase(answer)) {
            return false;
        }

        throw new InvalidConfirmationException(answer, DELETE_OPTION.text(), CANCEL_OPTION.text());
    }
}
