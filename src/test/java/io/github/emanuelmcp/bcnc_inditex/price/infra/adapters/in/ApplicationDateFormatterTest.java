package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationDateFormatterTest {
    private static final LocalDateTime DATE = LocalDateTime.of(2020, 6, 14, 16, 0, 0);

    private final ApplicationDateFormatter sut = new ApplicationDateFormatter();

    @Test
    void shouldParseDateWithTheExactFormat() {
        assertEquals(DATE, sut.parse("2020-06-14T16:00:00", Locale.ROOT));
    }

    @Test
    void shouldParseLeapDay() {
        assertEquals(LocalDateTime.of(2020, 2, 29, 16, 0, 0), sut.parse("2020-02-29T16:00:00", Locale.ROOT));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-06-14T16:00",
            "2020-06-14T16:00:00.5",
            "2020-06-14T16:00:00.123456789",
            "2020-06-14T16:00:00Z",
            "2020-06-14T16:00:00+05:00",
            "2020-06-14 16:00:00",
            "14-06-2020T16:00:00"
    })
    void shouldRejectDateThatDoesNotMatchTheExactFormat(String text) {
        assertThrows(DateTimeParseException.class, () -> sut.parse(text, Locale.ROOT));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2020-02-30T16:00:00", "2021-02-29T16:00:00", "2020-06-14T24:00:00", "2020-06-14T16:60:00"})
    void shouldRejectDateThatDoesNotExist(String text) {
        assertThrows(DateTimeParseException.class, () -> sut.parse(text, Locale.ROOT));
    }

    @Test
    void shouldPrintSecondsEvenWhenTheyAreZero() {
        assertEquals("2020-06-14T16:00:00", sut.print(DATE, Locale.ROOT));
    }
}