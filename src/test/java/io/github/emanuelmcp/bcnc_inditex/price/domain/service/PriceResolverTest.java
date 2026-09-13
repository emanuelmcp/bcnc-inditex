package io.github.emanuelmcp.bcnc_inditex.price.domain.service;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.ApplicationPeriod;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Money;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PriceResolverTest {
    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;

    private static final Price RATE_1 = buildPrice(1, "2020-06-14-00.00.00", "2020-12-31-23.59.59", 0, "35.50");
    private static final Price RATE_2 = buildPrice(2, "2020-06-14-15.00.00", "2020-06-14-18.30.00", 1, "25.45");
    private static final Price RATE_3 = buildPrice(3, "2020-06-15-00.00.00", "2020-06-15-11.00.00", 1, "30.50");
    private static final Price RATE_4 = buildPrice(4, "2020-06-15-16.00.00", "2020-12-31-23.59.59", 1, "38.95");

    private static final String CASE_1 = "2020-06-14-10.00.00";
    private static final String CASE_2 = "2020-06-14-16.00.00";
    private static final String CASE_3 = "2020-06-14-21.00.00";
    private static final String CASE_4 = "2020-06-15-10.00.00";
    private static final String CASE_5 = "2020-06-16-21.00.00";
    private static final String OUTSIDE_CASE = "2019-01-01-00.00.00";

    private static final List<Price> ALL_CANDIDATES = List.of(RATE_1, RATE_2, RATE_3, RATE_4);

    private final PriceResolver sut = new PriceResolver();

    @Test
    void shouldThrowNPEWhenApplicationDateIsNull() {
        assertThrows(NullPointerException.class, () -> sut.resolveApplicablePrice(null, ALL_CANDIDATES));
    }

    @Test
    void shouldThrowNPEWhenCandidatesIsNull() {
        assertThrows(NullPointerException.class, () -> sut.resolveApplicablePrice(parseDateFromRawData(CASE_1), null));
    }

    @Test
    void shouldReturnEmptyWhenCandidatesListIsEmpty() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(CASE_1), List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoCandidateIsApplicableOnThatDate() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(OUTSIDE_CASE), ALL_CANDIDATES);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTheOnlyCandidateWhenOnlyOneIsPresent() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(CASE_1), List.of(RATE_1));
        assertEquals(Optional.of(RATE_1), result);

    }

    @Test
    void shouldResolveRequestAt10On14DayFor35455Product() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(CASE_1), ALL_CANDIDATES);
        assertEquals(Optional.of(RATE_1), result);
    }

    @Test
    void shouldResolveRequestAt16On14DayFor35455Product() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(CASE_2), ALL_CANDIDATES);
        assertEquals(Optional.of(RATE_2), result);

    }
    @Test
    void shouldResolveRequestAt21On14DayFor35455Product() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(CASE_3), ALL_CANDIDATES);
        assertEquals(Optional.of(RATE_1), result);
    }

    @Test
    void shouldResolveRequestAt10On15DayFor35455Product() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(CASE_4), ALL_CANDIDATES);
        assertEquals(Optional.of(RATE_3), result);
    }

    @Test
    void shouldResolveRequestAt21On16DayFor35455Product() {
        Optional<Price> result = sut.resolveApplicablePrice(parseDateFromRawData(CASE_5), ALL_CANDIDATES);
        assertEquals(Optional.of(RATE_4), result);
    }

    private static Price buildPrice(Integer priceList, String start, String end, int priority, String amount) {
        return new Price(
                BRAND_ID,
                PRODUCT_ID,
                priceList,
                new ApplicationPeriod(parseDateFromRawData(start), parseDateFromRawData(end)), priority,
                new Money(new BigDecimal(amount), Currency.getInstance("EUR"))
        );
    }
    private static LocalDateTime parseDateFromRawData(String date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
        return LocalDateTime.parse(date, dateFormat);
    }
}