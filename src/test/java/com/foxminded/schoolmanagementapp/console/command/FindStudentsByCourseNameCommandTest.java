package com.foxminded.schoolmanagementapp.console.command;

import com.foxminded.schoolmanagementapp.console.ConsoleView;
import com.foxminded.schoolmanagementapp.console.MenuOption;
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
class FindStudentsByCourseNameCommandTest {

    private static final String FIELD_NAME = "Course name";

    @Mock
    private ConsoleInputReader inputReader;
    @Mock
    private ConsoleView view;
    @Mock
    private StudentService studentService;
    @InjectMocks
    private FindStudentsByCourseNameCommand command;

    @Test
    void menuOption_shouldReturnFindStudentsOption() {
        assertThat(command.menuOption())
                .isEqualTo(MenuOption.FIND_STUDENTS_BY_COURSE_NAME);
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

        ExecuteControl actual = command.execute();

        assertThat(actual).isEqualTo(ExecuteControl.CONTINUE);
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

        ExecuteControl actual = command.execute();

        assertThat(actual).isEqualTo(ExecuteControl.CONTINUE);
        verify(view).promptForCourseName();
        verify(studentService).findStudentsByCourseName(courseName);
        verify(view).showStudents(courseName, List.of());
    }
}
