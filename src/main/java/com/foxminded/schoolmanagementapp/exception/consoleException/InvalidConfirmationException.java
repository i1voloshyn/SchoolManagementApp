package com.foxminded.schoolmanagementapp.exception.consoleException;

public class InvalidConfirmationException extends ConsoleInputException {

    public InvalidConfirmationException(
            String actual, String confirmationOption, String cancellationOption) {
        super(
                "Confirmation must be '%s' or '%s', but was '%s'."
                        .formatted(confirmationOption, cancellationOption, actual));
    }
}
