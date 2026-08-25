package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.constants.ConfirmationOption;
import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.Field;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.constants.Prompt;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.InvalidConfirmationException;
import com.foxminded.schoolmanagementapp.service.EnrollmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Request to delete a student from course...");
        consoleView.promptForWriteOperationFlow(Prompt.STUDENT_ID.getValue());
        long studentId = inputReader.readPositiveLong(Field.STUDENT_ID.getValue());

        consoleView.promptForWriteOperationFlow(Prompt.COURSE_ID.getValue());
        long courseId = inputReader.readPositiveLong(Field.COURSE_ID.getValue());

        consoleView.promptForWriteOperationFlow(confirmationPrompt());
        String confirmationAnswer = inputReader.readRequiredText(Field.CONFIRMATION.getValue());

        if (isRemovalConfirmed(confirmationAnswer)) {
            enrollmentService.removeStudentFromCourse(studentId, courseId);
            consoleView.showSuccessMessageOnRemovalFromCourse();
        } else {
            log.info("Delete student action was cancelled by operator");
            consoleView.showCancellationMessageOnRemovalFromCourse();
        }
        log.info("Successfully deleted student with ID: {} from course with ID: {}", studentId, courseId);
        return LoopStatus.CONTINUE;
    }

    private String confirmationPrompt() {
        return Prompt.DELETE_STUDENT_FROM_COURSE_CONFIRMATION
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
