package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.repository.GreetingOutput;
import org.springframework.stereotype.Service;

@Service
public class ConsoleGreetingService implements GreetingOutput {
    @Override
    public void print(String greeting) {
        System.out.println(greeting);
    }
}
