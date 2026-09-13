package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.ApplicationPeriod;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Money;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PriceEntityMapperTest {

    private static final Long ID = 1L;
    private static final Integer BRAND_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2025, 6, 18, 10, 35);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2026, 6, 18, 10, 35);
    private static final Integer PRICE_LIST = 1;
    private static final Long PRODUCT_ID = 1L;
    private static final Integer PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(10);
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    private final PriceEntityMapper sut = new PriceEntityMapper();


    @Test
    void shouldMapPriceEntityToDomainWhenIsPerformed() {
        PriceEntity priceEntity = PriceEntity.builder()
                .id(ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .productId(PRODUCT_ID)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(CURRENCY.getCurrencyCode())
                .build();
        Price expectedPrice = new Price(
                BRAND_ID,
                PRODUCT_ID,
                PRICE_LIST,
                new ApplicationPeriod(START_DATE, END_DATE),
                PRIORITY,
                new Money(PRICE, CURRENCY)
        );
        Price result = sut.toDomain(priceEntity);
        assertEquals(expectedPrice, result);
    }

    @Test
    void shouldPropagateIllegalArgumentExceptionWhenEntityHasInvalidPriceRange() {
        PriceEntity invalidEntity = PriceEntity.builder()
                .id(ID)
                .brandId(BRAND_ID)
                .startDate(END_DATE)
                .endDate(START_DATE)
                .priceList(PRICE_LIST)
                .productId(PRODUCT_ID)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(CURRENCY.getCurrencyCode())
                .build();

        assertThrows(IllegalArgumentException.class, () -> sut.toDomain(invalidEntity));
    }

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = {"EURO", "eur", "XYZ", ""})
    void shouldPropagateIllegalArgumentExceptionWhenEntityHasInvalidCurrencyCode(String currencyCode) {
        PriceEntity invalidEntity = PriceEntity.builder()
                .id(ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .productId(PRODUCT_ID)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(currencyCode)
                .build();

        assertThrows(IllegalArgumentException.class, () -> sut.toDomain(invalidEntity));
    }
}