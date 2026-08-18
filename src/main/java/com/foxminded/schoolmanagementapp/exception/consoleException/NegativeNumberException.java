package com.foxminded.schoolmanagementapp.exception.consoleException;

public class NegativeNumberException extends ConsoleInputException {

    public NegativeNumberException(String fieldName, int actualValue) {
        super(
                "%s must be zero or greater, but was %d."
                        .formatted(fieldName, actualValue)
        );
    }
}