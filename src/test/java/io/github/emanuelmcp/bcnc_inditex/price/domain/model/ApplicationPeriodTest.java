package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationPeriodTest {
    private static final LocalDateTime DATE_1 = LocalDateTime.of(2025, 6, 18, 10, 35);
    private static final LocalDateTime DATE_2 = LocalDateTime.of(2026, 6, 18, 10, 35);

    @Test
    void shouldCreateAnInstanceWhenTheDataIsCorrect() {
        ApplicationPeriod applicationPeriod = new ApplicationPeriod(DATE_1, DATE_2);
        ApplicationPeriod expectedApplicationPeriod = new ApplicationPeriod(DATE_1, DATE_2);
        assertEquals(expectedApplicationPeriod, applicationPeriod);
    }

    @Test
    void shouldThrowNPEWhenStartIsNull() {
        assertThrows(NullPointerException.class, () -> new ApplicationPeriod(null, DATE_2));
    }

    @Test
    void shouldThrowNPEWhenEndIsNull() {
        assertThrows(NullPointerException.class, () -> new ApplicationPeriod(DATE_1, null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStartIsAfterThanEnd() {
        assertThrows(IllegalArgumentException.class, () -> new ApplicationPeriod(DATE_2, DATE_1));
    }

    @Test
    void shouldAllowStartEqualsToEnd() {
        assertDoesNotThrow(() -> new ApplicationPeriod(DATE_1, DATE_1));
    }
}