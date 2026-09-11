package com.foxminded.schoolmanagementapp.console.command;

import static com.foxminded.schoolmanagementapp.console.constants.ConfirmationOption.CANCEL;
import static com.foxminded.schoolmanagementapp.console.constants.ConfirmationOption.DELETE;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.Field;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.constants.Prompt;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.exception.consoleException.InvalidConfirmationException;
import com.foxminded.schoolmanagementapp.service.CourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class DeleteStudentFromCourseAction implements MenuActionRunner {
    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final CourseService courseService;

    @Override
    public MenuAction getAction() {
        return MenuAction.DELETE_STUDENT_FROM_COURSE;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(Prompt.STUDENT_ID.getValue());
        long studentId = inputReader.readPositiveLong(Field.STUDENT_ID.getValue());

        consoleView.promptForWriteOperationFlow(Prompt.COURSE_ID.getValue());
        long courseId = inputReader.readPositiveLong(Field.COURSE_ID.getValue());

        consoleView.promptForWriteOperationFlow(confirmationPrompt());
        String confirmationAnswer = inputReader.readRequiredText(Field.CONFIRMATION.getValue());

        log.info("Requested student deletion from course. StudentId={}, CourseId={}", studentId, courseId);

        if (isRemovalConfirmed(confirmationAnswer)) {
            courseService.removeStudentFromCourse(studentId, courseId);
            consoleView.showSuccessMessageOnRemovalFromCourse();
        } else {
            consoleView.showCancellationMessageOnRemovalFromCourse();
        }

        return LoopStatus.CONTINUE;
    }

    private String confirmationPrompt() {
        return Prompt.DELETE_STUDENT_FROM_COURSE_CONFIRMATION
                .getValue()
                .formatted(
                        DELETE.getValue(),
                        CANCEL.getValue());
    }

    private boolean isRemovalConfirmed(String answer) {
        if (DELETE.getValue().equalsIgnoreCase(answer)) {
            return true;
        }
        if (CANCEL.getValue().equalsIgnoreCase(answer)) {
            return false;
        }

        throw new InvalidConfirmationException(
                answer,
                DELETE.getValue(),
                CANCEL.getValue());
    }
}
