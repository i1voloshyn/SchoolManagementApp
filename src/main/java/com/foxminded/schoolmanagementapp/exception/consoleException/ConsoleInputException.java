package com.foxminded.schoolmanagementapp.exception.consoleException;

public abstract class ConsoleInputException extends RuntimeException {

    protected ConsoleInputException(String message) {
        super(message);
    }
}
