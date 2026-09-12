package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.ApplicationPeriod;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Money;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceRepositoryH2AdapterTest {
    private static final Long ID = 1L;
    private static final Integer BRAND_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2025, 6, 18, 10, 35);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2026, 6, 18, 10, 35);
    private static final LocalDateTime APPLICATION_DATE = START_DATE.plusDays(1);
    private static final Integer PRICE_LIST = 3;
    private static final Long PRODUCT_ID = 1L;
    private static final Integer PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(10);
    private static final String CURRENCY = "EUR";

    @Mock
    private JpaPriceRepository jpaPriceRepository;

    private final PriceEntityMapper priceEntityMapper = new PriceEntityMapper();

    private PriceRepositoryH2Adapter sut;

    @BeforeEach
    void setUp() {
        sut = new PriceRepositoryH2Adapter(jpaPriceRepository, priceEntityMapper);
    }

    @Test
    void shouldFindAndMapEachReturnedEntityToDomainWhenIsPerformed() {
        PriceEntity priceEntity = PriceEntity.builder()
                .id(ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .productId(PRODUCT_ID)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(CURRENCY)
                .build();

        when(jpaPriceRepository.findCandidates(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(priceEntity));

        List<Price> result = sut.findCandidates(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
        Price expectedPrice = new Price(
                BRAND_ID,
                PRODUCT_ID,
                PRICE_LIST,
                new ApplicationPeriod(START_DATE, END_DATE),
                PRIORITY,
                new Money(PRICE, CURRENCY)
        );
        assertEquals(List.of(expectedPrice), result);
    }

    @Test
    void shouldReturnEmptyListWhenJpaRepositoryReturnsNoEntities() {
        when(jpaPriceRepository.findCandidates(anyInt(), anyLong(), any())).thenReturn(List.of());
        List<Price> result = sut.findCandidates(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
        assertTrue(result.isEmpty());
    }
}