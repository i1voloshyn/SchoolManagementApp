package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.constants.Field;
import com.foxminded.schoolmanagementapp.console.constants.LoopStatus;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.constants.Prompt;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.service.EnrollmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Student enrollment requested...");
        consoleView.promptForWriteOperationFlow(Prompt.STUDENT_ID.getValue());
        long studentId = inputReader.readPositiveLong(Field.STUDENT_ID.getValue());

        consoleView.promptForWriteOperationFlow(Prompt.COURSE_ID.getValue());
        long courseId = inputReader.readPositiveLong(Field.COURSE_ID.getValue());

        enrollmentService.addStudentToCourse(studentId, courseId);

        log.info(
                "Student enrollment completed: studentId={}, courseId={}", studentId, courseId);
        consoleView.showSuccessMessageOnEnrollment();
        return LoopStatus.CONTINUE;
    }
}
