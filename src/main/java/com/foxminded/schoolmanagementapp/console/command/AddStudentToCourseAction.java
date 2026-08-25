package com.foxminded.schoolmanagementapp.console.command;

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
    private static final String STUDENT_ID = "Student ID";
    private static final String COURSE_ID = "Course ID";
    private static final String STUDENT_ID_PROMPT = "Enter student ID:";
    private static final String COURSE_ID_PROMPT = "Enter course ID:";

    private final ConsoleView consoleView;
    private final ConsoleInputReader inputReader;
    private final EnrollmentService enrollmentService;

    @Override
    public MenuAction getAction() {
        return MenuAction.ADD_STUDENT_TO_COURSE;
    }

    @Override
    public LoopStatus execute() {
        consoleView.promptForWriteOperationFlow(STUDENT_ID_PROMPT);
        long studentId = inputReader.readPositiveLong(STUDENT_ID);

        consoleView.promptForWriteOperationFlow(COURSE_ID_PROMPT);
        long courseId = inputReader.readPositiveLong(COURSE_ID);

        enrollmentService.addStudentToCourse(studentId, courseId);

        consoleView.showSuccessMessageOnEnrollment();
        return LoopStatus.CONTINUE;
    }
}
