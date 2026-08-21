package com.foxminded.schoolmanagementapp.exception.consoleException;

public class NonNumericInputException extends ConsoleInputException {

    public NonNumericInputException(String fieldName, String actualValue) {
        super(
                "%s must be a whole number, but was '%s'."
                        .formatted(fieldName, actualValue)
        );
    }
}