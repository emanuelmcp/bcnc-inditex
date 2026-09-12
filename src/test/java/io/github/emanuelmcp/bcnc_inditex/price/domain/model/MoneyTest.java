package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.0);
    private static final BigDecimal INCORRECT_AMOUNT = new BigDecimal("10.1234");
    private static final String CURRENCY = "EUR";


    @Test
    void shouldCreateAnInstanceWhenTheDataIsCorrect() {
        Money money = new Money(AMOUNT, CURRENCY);
        Money expectedMoney = new Money(AMOUNT, CURRENCY);
        assertEquals(expectedMoney, money);
    }

    @Test
    void shouldThrowNPEWhenAmountIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(null, CURRENCY));
    }

    @Test
    void shouldThrowNPEWhenCurrencyIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(AMOUNT, null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAmountIsLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.valueOf(-1), CURRENCY));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCurrencyIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Money(AMOUNT, ""));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenScaleIsGreaterThanTwo() {
        assertThrows(IllegalArgumentException.class, () -> new Money(INCORRECT_AMOUNT, CURRENCY));
    }

    @Test
    void shouldAllowZeroAmount() {
        assertDoesNotThrow(() -> new Money(BigDecimal.ZERO, CURRENCY));
    }

    @Test
    void equalsIsNotSensitiveToScale() {
        Money money1 = new Money(new BigDecimal("10.0"), CURRENCY);
        Money money2 = new Money(new BigDecimal("10.00"), CURRENCY);
        assertEquals(money1, money2);
    }
}