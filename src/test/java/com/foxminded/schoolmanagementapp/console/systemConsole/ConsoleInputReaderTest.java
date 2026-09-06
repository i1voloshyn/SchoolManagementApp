package com.foxminded.schoolmanagementapp.console.systemConsole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.foxminded.schoolmanagementapp.exception.consoleException.BlankConsoleInputException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NegativeNumberException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonNumericInputException;
import com.foxminded.schoolmanagementapp.exception.consoleException.NonPositiveNumberException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ConsoleInputReaderTest {

    @Nested
    class ReadActionNumberTests {

        @ParameterizedTest
        @ValueSource(ints = {Integer.MIN_VALUE, -10, -1, 0, 1, 2, 3, 4, 5, 100, Integer.MAX_VALUE})
        void shouldReturnIntegerAndIgnoreWhitespace(int expected) {
            ConsoleInputReader inputReader = inputReaderReturning("  " + expected + "  ");

            int actual = inputReader.readActionNumber();

            assertThat(actual).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"abc", "1.5", "1a", "2147483648"})
        void shouldThrowException_whenInputIsNotInteger(String inputValue) {
            ConsoleInputReader inputReader = inputReaderReturning(inputValue);

            assertThatExceptionOfType(NonNumericInputException.class)
                    .isThrownBy(inputReader::readActionNumber)
                    .withMessage(
                            "Menu item must be a whole number, but was '%s'."
                                    .formatted(inputValue));
        }
    }

    @Nested
    class ReadRequiredTextTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", " \n "})
        void shouldThrowException_whenInputIsBlank(String inputValue) {
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
        void shouldReturnTrimmedText(String inputValue, String expected) {
            ConsoleInputReader inputReader = inputReaderReturning(inputValue);

            String actual = inputReader.readRequiredText("Course name");

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class ReadNonNegativeIntegerTests {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10, Integer.MAX_VALUE})
        void shouldReturnValidNumber(int expected) {
            ConsoleInputReader inputReader = inputReaderReturning("  " + expected + "  ");

            int actual = inputReader.readNonNegativeInteger("Maximum student count");

            assertThat(actual).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -10, Integer.MIN_VALUE})
        void shouldThrowException_whenNumberIsNegative(int inputValue) {
            ConsoleInputReader inputReader = inputReaderReturning(String.valueOf(inputValue));

            assertThatExceptionOfType(NegativeNumberException.class)
                    .isThrownBy(() -> inputReader.readNonNegativeInteger("Maximum student count"))
                    .withMessage(
                            "Maximum student count must be zero or greater, but was %d."
                                    .formatted(inputValue));
        }

        @ParameterizedTest
        @ValueSource(strings = {"ten", "2.5", "1_000", "2147483648"})
        void shouldThrowException_whenInputIsNotInteger(String inputValue) {
            ConsoleInputReader inputReader = inputReaderReturning(inputValue);

            assertThatExceptionOfType(NonNumericInputException.class)
                    .isThrownBy(() -> inputReader.readNonNegativeInteger("Maximum student count"))
                    .withMessage(
                            "Maximum student count must be a whole number, but was '%s'."
                                    .formatted(inputValue));
        }
    }

    @Nested
    class ReadPositiveLongTests {

        @ParameterizedTest
        @ValueSource(longs = {1L, 10L, Long.MAX_VALUE})
        void shouldReturnValidNumber(long expected) {
            ConsoleInputReader inputReader = inputReaderReturning("  " + expected + "  ");

            long actual = inputReader.readPositiveLong("Student ID");

            assertThat(actual).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L})
        void shouldThrowException_whenNumberIsNotPositive(long inputValue) {
            ConsoleInputReader inputReader = inputReaderReturning(String.valueOf(inputValue));

            assertThatExceptionOfType(NonPositiveNumberException.class)
                    .isThrownBy(() -> inputReader.readPositiveLong("Student ID"))
                    .withMessage(
                            "Student ID must be greater than zero, but was %d."
                                    .formatted(inputValue));
        }

        @ParameterizedTest
        @ValueSource(strings = {"ten", "2.5", "1_000", "9223372036854775808"})
        void shouldThrowException_whenInputIsNotLong(String inputValue) {
            ConsoleInputReader inputReader = inputReaderReturning(inputValue);

            assertThatExceptionOfType(NonNumericInputException.class)
                    .isThrownBy(() -> inputReader.readPositiveLong("Student ID"))
                    .withMessage(
                            "Student ID must be a whole number, but was '%s'."
                                    .formatted(inputValue));
        }
    }

    private ConsoleInputReader inputReaderReturning(String inputValue) {
        return new ConsoleInputReader(() -> inputValue);
    }
}
