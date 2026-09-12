package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest(properties = "spring.sql.init.mode=never")
@Import({PriceRepositoryJpaAdapter.class, PriceEntityMapper.class})
class PriceRepositoryJpaAdapterIntegrationTest {
    private static final int BRAND_ID = 1;
    private static final long PRODUCT_ID = 35455L;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 16, 0, 0);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PriceRepositoryJpaAdapter sut;

    @Test
    void shouldOnlyReturnPricesOfTheRequestedBrandAndProduct() {
        persist(BRAND_ID, PRODUCT_ID, 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(2, PRODUCT_ID, 2, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, 35456L, 3, "2020-06-14T00:00:00", "2020-12-31T23:59:59");

        List<Price> result = sut.findCandidates(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        assertEquals(List.of(1), priceListsOf(result));
    }

    @Test
    void shouldOnlyReturnPricesWhosePeriodContainsTheDateIncludingBoundaries() {
        persist(BRAND_ID, PRODUCT_ID, 1, "2020-06-14T16:00:00", "2020-06-14T18:00:00");
        persist(BRAND_ID, PRODUCT_ID, 2, "2020-06-14T10:00:00", "2020-06-14T16:00:00");
        persist(BRAND_ID, PRODUCT_ID, 3, "2020-06-14T16:00:01", "2020-06-14T20:00:00");
        persist(BRAND_ID, PRODUCT_ID, 4, "2020-06-14T10:00:00", "2020-06-14T15:59:59");

        List<Price> result = sut.findCandidates(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        assertEquals(List.of(1, 2), priceListsOf(result));
    }

    private void persist(int brandId, long productId, int priceList, String start, String end) {
        entityManager.persistAndFlush(PriceEntity.builder()
                .brandId(brandId)
                .productId(productId)
                .priceList(priceList)
                .startDate(LocalDateTime.parse(start))
                .endDate(LocalDateTime.parse(end))
                .priority(0)
                .price(new BigDecimal("10.00"))
                .currency("EUR")
                .build());
    }

    private static List<Integer> priceListsOf(List<Price> prices) {
        return prices.stream().map(Price::priceList).sorted().toList();
    }
}