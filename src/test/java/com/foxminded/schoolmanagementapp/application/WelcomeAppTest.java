package com.foxminded.schoolmanagementapp.application;

import com.foxminded.schoolmanagementapp.repository.GreetingOutput;
import com.foxminded.schoolmanagementapp.repository.GreetingProvider;
import com.foxminded.schoolmanagementapp.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WelcomeAppTest {
    @Mock
    GreetingOutput output;
    @Mock
    GreetingProvider provider;
    @Mock
    private ApplicationArguments applicationArguments;

    @Test
    void run_shouldPrintExpectedMessage() {
        WelcomeApp welcomeApp = new WelcomeApp(output, provider);
        when(provider.getGreeting()).thenReturn("Hello");

        welcomeApp.run(applicationArguments);

        verify(provider).getGreeting();
        verify(output).print("Hello");
    }
}