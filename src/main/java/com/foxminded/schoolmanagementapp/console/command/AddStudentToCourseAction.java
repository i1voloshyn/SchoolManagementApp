package com.foxminded.schoolmanagementapp.console.command;

import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.COURSE_ID;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.COURSE_ID_PROMPT;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.STUDENT_ID;
import static com.foxminded.schoolmanagementapp.console.ConsoleMessage.STUDENT_ID_PROMPT;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.service.EnrollmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AddStudentToCourseAction implements MenuActionRunner {
    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final EnrollmentService enrollmentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.ADD_STUDENT_TO_COURSE;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(STUDENT_ID_PROMPT.text());
        long studentId = inputReader.readPositiveLong(STUDENT_ID.text());

        consoleView.promptForWriteOperationFlow(COURSE_ID_PROMPT.text());
        long courseId = inputReader.readPositiveLong(COURSE_ID.text());

        enrollmentService.addStudentToCourse(studentId, courseId);

        consoleView.showSuccessMessageOnEnrollment();
        return LoopStatus.CONTINUE;
    }
}
