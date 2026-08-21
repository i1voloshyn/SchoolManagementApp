package com.foxminded.schoolmanagementapp.console;

import com.foxminded.schoolmanagementapp.console.systemConsole.ConsoleOutput;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Group;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ConsoleView {

    private final ConsoleOutput output;

    public void showGreeting() {
        output.writeLine("Welcome to School Management App!");
    }

    public void showMenu() {
        output.writeLine("");
        output.writeLine("Select an action:");

        for (MenuAction option : MenuAction.values()) {
            output.writeLine(
                    "%d - %s".formatted(
                            option.number(),
                            option.description()
                    )
            );
        }

        output.writeLine("Enter menu item:");
    }

    public void promptForMaximumStudentCount() {
        output.writeLine("Enter maximum student count:");
    }

    public void promptForCourseName() {
        output.writeLine("Enter course name:");
    }

    public void showGroups(List<Group> groups) {
        if (groups.isEmpty()) {
            output.writeLine("No groups found.");
            return;
        }

        output.writeLine("ID | NAME");

        groups.forEach(group -> output.writeLine(
                "%s | %s".formatted(
                        group.getId(),
                        group.getName()
                )
        ));
    }

    public void showStudents(
            String courseName,
            List<StudentDto> students
    ) {
        if (students.isEmpty()) {
            output.writeLine(
                    "No students found for course: " + courseName
            );
            return;
        }

        output.writeLine("ID | GROUP ID | FIRST NAME | LAST NAME");

        students.forEach(student -> output.writeLine(
                "%s | %s | %s | %s".formatted(
                        student.id(),
                        student.groupId() == null
                                ? "-"
                                : student.groupId(),
                        student.firstName(),
                        student.lastName()
                )
        ));
    }

    public void showCourses(List<CourseDto> courses) {
        if (courses.isEmpty()) {
            output.writeLine("No courses found.");
            return;
        }

        output.writeLine("ID | NAME | DESCRIPTION");

        courses.forEach(course -> output.writeLine(
                "%s | %s | %s".formatted(
                        course.id(),
                        course.name(),
                        course.description()
                )
        ));
    }

    public void showInputError(String message) {
        output.writeLine("Invalid input: " + message);
    }

    public void showGoodbye() {
        output.writeLine("Goodbye!");
    }
}