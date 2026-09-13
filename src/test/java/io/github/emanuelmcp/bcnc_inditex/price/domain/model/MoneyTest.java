package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.0);
    private static final Currency CURRENCY = Currency.getInstance("EUR");


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

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({"10.1234, EUR", "1000.5, JPY", "1.2345, KWD"})
    void shouldThrowIllegalArgumentExceptionWhenAmountHasMoreDecimalsThanItsCurrencyAllows(BigDecimal amount, Currency currency) {
        assertThrows(IllegalArgumentException.class, () -> new Money(amount, currency));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCurrencyHasNoMinorUnit() {
        assertThrows(IllegalArgumentException.class, () -> new Money(AMOUNT, Currency.getInstance("XAU")));
    }

    @ParameterizedTest(name = "{0} {1} -> {2}")
    @CsvSource({"35.5, EUR, 35.50", "10.5000, EUR, 10.50", "1000.00, JPY, 1000", "1.23, KWD, 1.230"})
    void shouldScaleAmountToTheMinorUnitsOfItsCurrency(BigDecimal amount, Currency currency, BigDecimal expected) {
        assertEquals(expected, new Money(amount, currency).amount());
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