package com.foxminded.schoolmanagementapp.console.systemConsole;

import com.foxminded.schoolmanagementapp.exception.consoleException.BlankConsoleInputException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NegativeNumberException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonNumericInputException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonPositiveNumberException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConsoleInputReader {
    private static final String MENU_ITEM = "Menu item";

    private final ConsoleInput input;

    public int readActionNumber() {
        String value = readRequiredText(MENU_ITEM);
        return parseInteger(value, MENU_ITEM);
    }

    public int readNonNegativeInteger(String fieldName) {
        String value = readRequiredText(fieldName);
        int number = parseInteger(value, fieldName);

        if (number < 0) {
            throw new NegativeNumberException(fieldName, number);
        }

        return number;
    }

    public long readPositiveLong(String fieldName) {
        String value = readRequiredText(fieldName);
        long number = parseLong(value, fieldName);

        if (number <= 0) {
            throw new NonPositiveNumberException(fieldName, number);
        }

        return number;
    }

    public String readRequiredText(String fieldName) {
        String value = input.readLine();

        if (value == null || value.isBlank()) {
            throw new BlankConsoleInputException(fieldName);
        }

        return value.trim();
    }

    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new NonNumericInputException(fieldName, value);
        }
    }

    private long parseLong(String value, String fieldName) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new NonNumericInputException(fieldName, value);
        }
    }
}
