package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.LoopStatus;
import com.foxminded.schoolmanagementapp.console.MenuAction;
import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleInputReader;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindStudentsByCourseNameActionTest {

    private static final String FIELD_NAME = "Course name";

    @Mock
    private ConsoleInputReader inputReader;
    @Mock
    private ConsoleView view;
    @Mock
    private StudentService studentService;
    @InjectMocks
    private FindStudentsByCourseNameAction command;

    @Test
    void getAction() {
        assertThat(command.getAction())
                .isEqualTo(MenuAction.FIND_STUDENTS_BY_COURSE_NAME);
    }

    @Test
    void execute_shouldReadCourseNameAndDisplayMatchingStudents() {
        String courseName = "Java";
        List<StudentDto> students = List.of(
                new StudentDto(1L, 10L, "John", "Smith"),
                new StudentDto(2L, null, "Anna", "Brown")
        );
        when(inputReader.readRequiredText(FIELD_NAME))
                .thenReturn(courseName);
        when(studentService.findStudentsByCourseName(courseName))
                .thenReturn(students);

        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(view).promptForCourseName();
        verify(inputReader).readRequiredText(FIELD_NAME);
        verify(studentService).findStudentsByCourseName(courseName);
        verify(view).showStudents(courseName, students);
    }

    @Test
    void execute_shouldDisplayEmptyResult_whenCourseDoesNotExist() {
        String courseName = "Unknown";
        when(inputReader.readRequiredText(FIELD_NAME))
                .thenReturn(courseName);
        when(studentService.findStudentsByCourseName(courseName))
                .thenThrow(new CourseNotFoundException(
                        "Course not found with name: " + courseName
                ));

        LoopStatus actual = command.execute();

        assertThat(actual).isEqualTo(LoopStatus.CONTINUE);
        verify(view).promptForCourseName();
        verify(studentService).findStudentsByCourseName(courseName);
        verify(view).showStudents(courseName, List.of());
    }
}
