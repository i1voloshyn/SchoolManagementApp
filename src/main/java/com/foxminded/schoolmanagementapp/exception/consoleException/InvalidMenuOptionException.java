package com.foxminded.schoolmanagementapp.exception.consoleException;

public class InvalidMenuOptionException extends ConsoleInputException {

    public InvalidMenuOptionException(int actualValue, int lastValue) {
        super(
                "Invalid menu item '%d'. Choose a number from 0 to %d."
                        .formatted(actualValue, lastValue));
    }
}
