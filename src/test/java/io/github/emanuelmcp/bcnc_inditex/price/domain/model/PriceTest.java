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
}