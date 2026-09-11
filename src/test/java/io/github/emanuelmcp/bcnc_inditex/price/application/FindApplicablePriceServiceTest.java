package io.github.emanuelmcp.bcnc_inditex.price.application;

import io.github.emanuelmcp.bcnc_inditex.price.domain.exception.PriceNotFoundException;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.ApplicationPeriod;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Money;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceQuery;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;
import io.github.emanuelmcp.bcnc_inditex.price.domain.service.PriceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceServiceTest {
    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;
    private static final Integer PRICE_LIST = 1;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(35.50);
    private static final String CURRENCY = "EUR";
    private static final Integer PRIORITY = 0;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0, 0);
    private static final FindApplicablePriceQuery QUERY = new FindApplicablePriceQuery(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

    @Mock
    private PriceRepository priceRepository;

    private FindApplicablePriceService sut;

    @BeforeEach
    void setUp() {
        sut = new FindApplicablePriceService(priceRepository, new PriceResolver());
    }

    @Test
    void shouldReturnThePriceWhenThereIsOneApplicableCandidate() {
        ApplicationPeriod applicationPeriod = new ApplicationPeriod(APPLICATION_DATE.minusDays(1), APPLICATION_DATE.plusDays(1));
        Money money = new Money(AMOUNT, CURRENCY);
        Price expected = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, applicationPeriod, PRIORITY , money);
        when(priceRepository.findCandidates(anyInt(), anyLong(), any())).thenReturn(List.of(expected));
        Price result = sut.findApplicablePrice(QUERY);
        verify(priceRepository).findCandidates(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
        assertEquals(expected, result);
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenThereAreNoCandidates() {
        when(priceRepository.findCandidates(anyInt(), anyLong(), any())).thenReturn(List.of());
        assertThrows(PriceNotFoundException.class, () -> sut.findApplicablePrice(QUERY));
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenNoCandidateIsApplicableOnThatDate() {
        ApplicationPeriod applicationPeriod = new ApplicationPeriod(APPLICATION_DATE.plusDays(10), APPLICATION_DATE.plusDays(20));
        Money money = new Money(AMOUNT, CURRENCY);
        Price notApplicablePrice = new Price(BRAND_ID, PRODUCT_ID, PRICE_LIST, applicationPeriod, PRIORITY , money);
        when(priceRepository.findCandidates(anyInt(), anyLong(), any())).thenReturn(List.of(notApplicablePrice));
        assertThrows(PriceNotFoundException.class, () -> sut.findApplicablePrice(QUERY));
    }
}