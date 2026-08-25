package com.foxminded.schoolmanagementapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.foxminded.schoolmanagementapp.console.ConsoleMenu;
import com.foxminded.schoolmanagementapp.console.constants.MenuAction;
import com.foxminded.schoolmanagementapp.console.MenuActionRunnerDispatcher;
import com.foxminded.schoolmanagementapp.console.command.MenuActionRunner;
import com.foxminded.schoolmanagementapp.service.CourseService;
import com.foxminded.schoolmanagementapp.service.EnrollmentService;
import com.foxminded.schoolmanagementapp.service.GroupService;
import com.foxminded.schoolmanagementapp.service.StudentService;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = SchoolManagementAppApplicationTests.ConsoleWiringConfiguration.class)
class SchoolManagementAppApplicationTests {

    @Autowired private List<MenuActionRunner> commands;
    @Autowired private MenuActionRunnerDispatcher dispatcher;
    @Autowired private ConsoleMenu consoleMenu;

    @Test
    void context_shouldRegisterCommandForEveryMenuOption() {
        assertThat(commands)
                .hasSize(MenuAction.values().length)
                .extracting(MenuActionRunner::getAction)
                .containsExactlyInAnyOrder(MenuAction.values());

        assertThat(dispatcher).isNotNull();
        assertThat(consoleMenu).isNotNull();
    }

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(basePackageClasses = ConsoleMenu.class)
    static class ConsoleWiringConfiguration {

        @Bean
        Scanner scanner() {
            return new Scanner(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8);
        }

        @Bean
        GroupService groupService() {
            return mock(GroupService.class);
        }

        @Bean
        StudentService studentService() {
            return mock(StudentService.class);
        }

        @Bean
        CourseService courseService() {
            return mock(CourseService.class);
        }

        @Bean
        EnrollmentService enrollmentService() {
            return mock(EnrollmentService.class);
        }
    }
}
