package com.foxminded.schoolmanagementapp.exception.consoleException;

public class BlankConsoleInputException extends ConsoleInputException {

    public BlankConsoleInputException(String fieldName) {
        super(fieldName + " must not be empty.");
    }
}