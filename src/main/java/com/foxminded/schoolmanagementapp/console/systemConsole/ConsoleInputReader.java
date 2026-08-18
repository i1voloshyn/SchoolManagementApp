package com.foxminded.schoolmanagementapp.console.systemConsole;

import com.foxminded.schoolmanagementapp.console.MenuOption;
import com.foxminded.schoolmanagementapp.exception.consoleException.BlankConsoleInputException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NegativeNumberException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonNumericInputException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConsoleInputReader {

    private final static String MENU_ITEM = "Menu item";

    private final ConsoleInput input;

    public MenuOption readMenuOption() {
        String value = readRequiredText(MENU_ITEM);
        int optionNumber = parseInteger(value, MENU_ITEM);

        return MenuOption.fromNumber(optionNumber);
    }

    public int readNonNegativeInteger(String fieldName) {
        String value = readRequiredText(fieldName);
        int number = parseInteger(value, fieldName);

        if (number < 0) {
            throw new NegativeNumberException(fieldName, number);
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
}