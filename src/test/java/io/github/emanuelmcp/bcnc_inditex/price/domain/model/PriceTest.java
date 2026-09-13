package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {
    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 1L;
    private static final Integer PRICE_LIST = 1;
    private static final ApplicationPeriod APPLICATION_PERIOD = new ApplicationPeriod(
            LocalDateTime.of(2025, 6, 18, 10, 35),
            LocalDateTime.of(2026, 6, 18, 10, 35)
    );
    private static final Integer PRIORITY = 1;
    private static final Money MONEY = new Money(BigDecimal.valueOf(10), Currency.getInstance("EUR"));

    @Test
    void shouldCreatePriceWhenDataIsCorrect() {
        Price expectedPrice = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, PRIORITY, MONEY);
        Price price = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, PRIORITY, MONEY);
        assertEquals(expectedPrice, price);
    }

    @Test
    void shouldThrowNPEWhenBrandIdIsNull() {
        assertThrows(NullPointerException.class, () -> new Price(null, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, PRIORITY, MONEY));
    }

    @Test
    void shouldThrowNPEWhenProductIdIsNull() {
        assertThrows(NullPointerException.class, () -> new Price(BRAND_ID, null, PRICE_LIST, APPLICATION_PERIOD, PRIORITY, MONEY));
    }

    @Test
    void shouldThrowNPEWhenPriceListIsNull() {
        assertThrows(NullPointerException.class, () -> new Price(BRAND_ID, PRODUCT_ID, null, APPLICATION_PERIOD, PRIORITY, MONEY));
    }

    @Test
    void shouldThrowNPEWhenApplicationPeriodIsNull() {
        assertThrows(NullPointerException.class, () -> new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, null, PRIORITY, MONEY));
    }

    @Test
    void shouldThrowNPEWhenPriorityIsNull() {
        assertThrows(NullPointerException.class, () -> new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, null, MONEY));
    }

    @Test
    void shouldThrowNPEWhenMoneyIsNull() {
        assertThrows(NullPointerException.class, () -> new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, PRIORITY, null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPriorityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, -1, MONEY));
    }

    @Test
    void shouldAllowZeroPriority() {
        assertDoesNotThrow(() -> new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, 0, MONEY));
    }

    @Test
    void shouldBeApplicableWhenDateIsWithinItsPeriod() {
        Price price = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, PRIORITY, MONEY);
        assertTrue(price.isApplicableOn(APPLICATION_PERIOD.start().plusDays(1)));
    }

    @Test
    void shouldNotBeApplicableWhenDateIsOutsideItsPeriod() {
        Price price = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, PRIORITY, MONEY);
        assertFalse(price.isApplicableOn(APPLICATION_PERIOD.end().plusDays(1)));
    }

    @Test
    void shouldRankHigherPriorityAbove() {
        Price lowPriority = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, 0, MONEY);
        Price highPriority = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, APPLICATION_PERIOD, 1, MONEY);
        assertTrue(Price.byApplicationPriority().compare(lowPriority, highPriority) < 0);
    }

    @Test
    void shouldBreakPriorityTieByMostRecentStartDate() {
        ApplicationPeriod earlierPeriod = new ApplicationPeriod(APPLICATION_PERIOD.start(), APPLICATION_PERIOD.end());
        ApplicationPeriod laterPeriod = new ApplicationPeriod(APPLICATION_PERIOD.start().plusDays(1), APPLICATION_PERIOD.end());

        Price earlierStart = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, earlierPeriod, PRIORITY, MONEY);
        Price laterStart = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, laterPeriod, PRIORITY, MONEY);

        assertTrue(Price.byApplicationPriority().compare(earlierStart, laterStart) < 0);
    }
}