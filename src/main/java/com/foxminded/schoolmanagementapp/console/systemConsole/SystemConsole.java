package com.foxminded.schoolmanagementapp.console.systemConsole;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@AllArgsConstructor
@Component
public class SystemConsole implements ConsoleInput, ConsoleOutput {

    private final Scanner scanner;

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public void writeLine(String message) {
        System.out.println(message);
    }
}
