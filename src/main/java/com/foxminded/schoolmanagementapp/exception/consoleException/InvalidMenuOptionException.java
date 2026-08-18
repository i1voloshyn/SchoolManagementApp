package com.foxminded.schoolmanagementapp.exception.consoleException;

public class InvalidMenuOptionException extends ConsoleInputException {

    public InvalidMenuOptionException(int actualValue) {
        super(
                "Invalid menu item '%d'. Choose a number from 0 to 4."
                        .formatted(actualValue)
        );
    }
}