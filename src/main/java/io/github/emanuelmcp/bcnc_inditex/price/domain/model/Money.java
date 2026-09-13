package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount can not be null");
        Objects.requireNonNull(currency, "Currency can not be null");

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount can not be negative");
        }
        amount = toMinorUnitScale(amount, currency);
    }

    private BigDecimal toMinorUnitScale(BigDecimal amount, Currency currency) {
        int fractionDigits = currency.getDefaultFractionDigits();
        if (fractionDigits < 0) {
            throw new IllegalArgumentException("Currency %s has no minor unit".formatted(currency.getCurrencyCode()));
        }
        try {
            return amount.setScale(fractionDigits, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Amount can not have more than %d decimals for %s"
                    .formatted(fractionDigits, currency.getCurrencyCode()), e);
        }
    }
}