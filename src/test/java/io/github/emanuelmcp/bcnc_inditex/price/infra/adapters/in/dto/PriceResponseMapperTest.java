package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in.dto;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.ApplicationPeriod;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Money;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PriceResponseMapperTest {
    private static final Integer BRAND_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2025, 6, 18, 10, 35);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2026, 6, 18, 10, 35);
    private static final Integer PRICE_LIST = 1;
    private static final Long PRODUCT_ID = 1L;
    private static final Integer PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(10);
    private static final BigDecimal EXPECTED_PRICE = PRICE.setScale(2, RoundingMode.UNNECESSARY);
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    private final PriceResponseMapper sut = new PriceResponseMapper();

    @Test
    void shouldMapEntityToDtoWhenIsPerformed() {
        Price price = new Price(
                BRAND_ID,
                PRODUCT_ID,
                PRICE_LIST,
                new ApplicationPeriod(START_DATE, END_DATE),
                PRIORITY,
                new Money(PRICE, CURRENCY)
        );
        PriceResponseDto expectedDto = new PriceResponseDto(PRODUCT_ID, BRAND_ID, PRICE_LIST, START_DATE, END_DATE, EXPECTED_PRICE, CURRENCY.getCurrencyCode());

        PriceResponseDto result = sut.toResponse(price);

        assertEquals(expectedDto, result);
    }
}