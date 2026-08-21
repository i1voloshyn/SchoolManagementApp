package com.foxminded.schoolmanagementapp.application;

import com.foxminded.schoolmanagementapp.console.ConsoleMenu;
import com.foxminded.schoolmanagementapp.util.datagenerator.DataGenerator;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "school.console.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WelcomeApp implements ApplicationRunner {
    private final DataGenerator dataGenerator;
    private final ConsoleMenu consoleMenu;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        dataGenerator.generateDataIfEmpty();
        consoleMenu.start();
    }
}
