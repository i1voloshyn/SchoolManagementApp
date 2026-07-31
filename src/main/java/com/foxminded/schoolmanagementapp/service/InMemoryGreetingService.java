package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.repository.GreetingProvider;
import org.springframework.stereotype.Service;

@Service
public class InMemoryGreetingService implements GreetingProvider {
    @Override
    public String getGreeting() {
        return "Welcome to School Management App!";
    }
}
