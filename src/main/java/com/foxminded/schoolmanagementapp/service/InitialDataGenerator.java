package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.config.DataGeneratorProperties;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.util.datagenerator.CoursesGenerator;
import com.foxminded.schoolmanagementapp.util.datagenerator.DataGenerator;
import com.foxminded.schoolmanagementapp.util.datagenerator.GroupsGenerator;
import com.foxminded.schoolmanagementapp.util.datagenerator.StudentGenerator;
import com.foxminded.schoolmanagementapp.util.enrollment.EnrollmentsRule;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class InitialDataGenerator implements DataGenerator {

    private final GroupService groupService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final SchoolDataStateService schoolDataStateService;

    private final DataGeneratorProperties properties;
    private final GroupsGenerator groupsGenerator;
    private final CoursesGenerator coursesGenerator;
    private final StudentGenerator studentGenerator;
    private final EnrollmentsRule enrollmentsRule;

    @Override
    @Transactional
    public boolean generateDataIfEmpty() {
        if (!schoolDataStateService.isDatabaseEmpty()) {

            log.info("Initial data generation skipped: reason=database-not-empty");
            return false;
        }
        log.info("Data generation started");

        List<Group> groups = createGroups();
        List<CourseDto> courses = createCourses();
        List<StudentDto> students = createStudentsWithGroups(groups);
        List<Enrollment> enrollments = enrollmentsRule.apply(students, courses);

        log.info(" Initial data generation completed: " +
                        "groupsCount={}, coursesCount={}, studentsCount={}, enrollmentsCount={}",
                properties.groupsCount(), properties.coursesCount(), properties.studentsCount(), enrollments.size());

        courseService.addStudentsToCourses(enrollments);

        log.info("Data generation finished successfully");
        return true;
    }

    private List<Group> createGroups() {
        return groupsGenerator.generateGroups(properties.groupsCount()).stream()
                .map(groupService::createGroup)
                .toList();
    }

    private List<CourseDto> createCourses() {
        return coursesGenerator.generateCourses().stream()
                .map(courseService::createCourse)
                .toList();
    }

    private List<StudentDto> createStudentsWithGroups(List<Group> groups) {
        List<Long> groupIds = groups.stream().map(Group::getId).toList();

        var studentsWithCourses = studentGenerator.generateStudentsWithGroups(groupIds).stream()
                .toList();


        return studentService.addStudents(studentsWithCourses);
    }
}
