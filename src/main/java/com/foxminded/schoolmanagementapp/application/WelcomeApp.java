package com.foxminded.schoolmanagementapp.application;

import com.foxminded.schoolmanagementapp.repository.GreetingOutput;
import com.foxminded.schoolmanagementapp.repository.GreetingProvider;
import com.foxminded.schoolmanagementapp.repository.GroupRepository;
import com.foxminded.schoolmanagementapp.util.datagenerator.DataGenerator;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class WelcomeApp implements ApplicationRunner {
    private final GreetingOutput greetingOutput;
    private final GreetingProvider greetingProvider;
    private final DataGenerator dataGenerator;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        dataGenerator.generateDataIfEmpty();

        String greeting = greetingProvider.getGreeting();
        greetingOutput.print(greeting);
    }
}
