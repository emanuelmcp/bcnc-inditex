package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount can not be null");
        Objects.requireNonNull(currency, "Currency can not be null");

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount can not be negative");
        }
        if (currency.isBlank()) {
            throw new IllegalArgumentException("Currency can not be blank");
        }
        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
    }
}
