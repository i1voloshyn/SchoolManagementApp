package com.foxminded.schoolmanagementapp.console.systemConsole;

import com.foxminded.schoolmanagementapp.exception.consoleException.BlankConsoleInputException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NegativeNumberException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonNumericInputException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ConsoleInputReaderTest {

    @ParameterizedTest
    @ValueSource(ints = {
            Integer.MIN_VALUE,
            -10,
            -1,
            0,
            1,
            2,
            3,
            4,
            5,
            100,
            Integer.MAX_VALUE
    })
    void readActionNumber_shouldReturnIntegerAndIgnoreWhitespace(int expected) {
        ConsoleInputReader inputReader = inputReaderReturning(
                "  " + expected + "  "
        );

        int actual = inputReader.readActionNumber();

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1.5", "1a", "2147483648"})
    void readActionNumber_shouldThrowException_whenInputIsNotInteger(
            String inputValue
    ) {
        ConsoleInputReader inputReader = inputReaderReturning(inputValue);

        assertThatExceptionOfType(NonNumericInputException.class)
                .isThrownBy(inputReader::readActionNumber)
                .withMessage(
                        "Menu item must be a whole number, but was '%s'."
                                .formatted(inputValue)
                );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", " \n "})
    void readRequiredText_shouldThrowException_whenInputIsBlank(
            String inputValue
    ) {
        ConsoleInputReader inputReader = inputReaderReturning(inputValue);

        assertThatExceptionOfType(BlankConsoleInputException.class)
                .isThrownBy(() -> inputReader.readRequiredText("Course name"))
                .withMessage("Course name must not be empty.");
    }

    @ParameterizedTest
    @CsvSource({
            "Java, Java",
            "' Java ', Java",
            "'  SQL  ', SQL",
            "'Spring Boot', 'Spring Boot'"
    })
    void readRequiredText_shouldReturnTrimmedText(
            String inputValue,
            String expected
    ) {
        ConsoleInputReader inputReader = inputReaderReturning(inputValue);

        String actual = inputReader.readRequiredText("Course name");

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, Integer.MAX_VALUE})
    void readNonNegativeInteger_shouldReturnValidNumber(int expected) {
        ConsoleInputReader inputReader = inputReaderReturning(
                "  " + expected + "  "
        );

        int actual = inputReader.readNonNegativeInteger(
                "Maximum student count"
        );

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, Integer.MIN_VALUE})
    void readNonNegativeInteger_shouldThrowException_whenNumberIsNegative(
            int inputValue
    ) {
        ConsoleInputReader inputReader = inputReaderReturning(
                String.valueOf(inputValue)
        );

        assertThatExceptionOfType(NegativeNumberException.class)
                .isThrownBy(() -> inputReader.readNonNegativeInteger(
                        "Maximum student count"
                ))
                .withMessage(
                        "Maximum student count must be zero or greater, but was %d."
                                .formatted(inputValue)
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ten", "2.5", "1_000", "2147483648"})
    void readNonNegativeInteger_shouldThrowException_whenInputIsNotInteger(
            String inputValue
    ) {
        ConsoleInputReader inputReader = inputReaderReturning(inputValue);

        assertThatExceptionOfType(NonNumericInputException.class)
                .isThrownBy(() -> inputReader.readNonNegativeInteger(
                        "Maximum student count"
                ))
                .withMessage(
                        "Maximum student count must be a whole number, but was '%s'."
                                .formatted(inputValue)
                );
    }

    private ConsoleInputReader inputReaderReturning(String inputValue) {
        return new ConsoleInputReader(() -> inputValue);
    }
}
