package io.github.emanuelmcp.bcnc_inditex.price.domain.port.in;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FindApplicablePriceQueryTest {
    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 1L;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2026, 6, 18, 10, 35);

    @Test
    void shouldCreateFindApplicablePriceQueryWhenDataIsCorrect() {
        FindApplicablePriceQuery expectedFindApplicablePriceQuery = new FindApplicablePriceQuery(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
        FindApplicablePriceQuery findApplicablePriceQuery = new FindApplicablePriceQuery(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
        assertEquals(expectedFindApplicablePriceQuery, findApplicablePriceQuery);
    }

    @Test
    void shouldThrowNPEWhenBrandIdIsNull() {
        assertThrows(NullPointerException.class, () -> new FindApplicablePriceQuery(null, PRODUCT_ID, APPLICATION_DATE));
    }

    @Test
    void shouldThrowNPEWhenProductIdIsNull() {
        assertThrows(NullPointerException.class, () -> new FindApplicablePriceQuery(BRAND_ID, null, APPLICATION_DATE));
    }

    @Test
    void shouldThrowNPEWhenApplicationDateIsNull() {
        assertThrows(NullPointerException.class, () -> new FindApplicablePriceQuery(BRAND_ID, PRODUCT_ID, null));
    }

    @Test
    void shouldTruncateApplicationDateToSeconds() {
        FindApplicablePriceQuery query = new FindApplicablePriceQuery(BRAND_ID, PRODUCT_ID, APPLICATION_DATE.plusNanos(500_000_000));
        assertEquals(APPLICATION_DATE, query.applicationDate());
    }
}