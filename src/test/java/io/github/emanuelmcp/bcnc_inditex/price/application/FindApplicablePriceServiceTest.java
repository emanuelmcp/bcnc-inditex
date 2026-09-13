package io.github.emanuelmcp.bcnc_inditex.price.application;

import io.github.emanuelmcp.bcnc_inditex.price.domain.exception.PriceNotFoundException;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.ApplicationPeriod;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Money;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceQuery;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceServiceTest {
    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0, 0);
    private static final FindApplicablePriceQuery QUERY = new FindApplicablePriceQuery(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    private static final Price PRICE = new Price(
            BRAND_ID,
            PRODUCT_ID,
            1,
            new ApplicationPeriod(APPLICATION_DATE.minusDays(1), APPLICATION_DATE.plusDays(1)),
            0,
            new Money(BigDecimal.valueOf(35.50), Currency.getInstance("EUR"))
    );

    @Mock
    private PriceRepository priceRepository;

    private FindApplicablePriceService sut;

    @BeforeEach
    void setUp() {
        sut = new FindApplicablePriceService(priceRepository);
    }

    @Test
    void shouldReturnThePriceFoundByTheRepository() {
        when(priceRepository.findApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)).thenReturn(Optional.of(PRICE));
        assertEquals(PRICE, sut.findApplicablePrice(QUERY));
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenTheRepositoryFindsNoPrice() {
        when(priceRepository.findApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)).thenReturn(Optional.empty());
        assertThrows(PriceNotFoundException.class, () -> sut.findApplicablePrice(QUERY));
    }
}