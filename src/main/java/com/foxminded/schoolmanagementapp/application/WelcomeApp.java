package com.foxminded.schoolmanagementapp.application;

import com.foxminded.schoolmanagementapp.repository.GreetingOutput;
import com.foxminded.schoolmanagementapp.repository.GreetingProvider;
import com.foxminded.schoolmanagementapp.repository.GroupRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class WelcomeApp implements ApplicationRunner {
    private final GreetingOutput greetingOutput;
    private final GreetingProvider greetingProvider;

    public WelcomeApp(GreetingOutput greetingOutput, GreetingProvider greetingProvider) {
        this.greetingOutput = greetingOutput;
        this.greetingProvider = greetingProvider;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        String greeting = greetingProvider.getGreeting();
        greetingOutput.print(greeting);
    }
}
