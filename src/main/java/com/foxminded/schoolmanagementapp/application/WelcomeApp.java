package com.foxminded.schoolmanagementapp.application;

import com.foxminded.schoolmanagementapp.console.ConsoleMenu;
import com.foxminded.schoolmanagementapp.util.datagenerator.DataGenerator;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class WelcomeApp implements ApplicationRunner {
    private final DataGenerator dataGenerator;
    private final ConsoleMenu consoleMenu;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        dataGenerator.generateDataIfEmpty();
        consoleMenu.start();
    }
}
