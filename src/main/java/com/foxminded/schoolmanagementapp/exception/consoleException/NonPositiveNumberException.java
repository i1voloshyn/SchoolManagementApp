package com.foxminded.schoolmanagementapp.exception.consoleException;

public class NonPositiveNumberException extends ConsoleInputException {

    public NonPositiveNumberException(String fieldName, long number) {
        super(fieldName + " must be greater than zero, but was " + number + ".");
    }
}
