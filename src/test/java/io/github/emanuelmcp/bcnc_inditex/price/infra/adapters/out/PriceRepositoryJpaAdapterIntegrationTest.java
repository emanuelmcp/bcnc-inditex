package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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

    @ParameterizedTest(name = "{0} -> tarifa {1}")
    @CsvSource({
            "2020-06-14T10:00:00, 1",
            "2020-06-14T16:00:00, 2",
            "2020-06-14T21:00:00, 1",
            "2020-06-15T10:00:00, 3",
            "2020-06-16T21:00:00, 4"
    })
    void shouldResolveTheScenariosOfTheStatement(LocalDateTime applicationDate, int expectedPriceList) {
        persist(BRAND_ID, PRODUCT_ID, 1, 0, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 2, 1, "2020-06-14T15:00:00", "2020-06-14T18:30:00");
        persist(BRAND_ID, PRODUCT_ID, 3, 1, "2020-06-15T00:00:00", "2020-06-15T11:00:00");
        persist(BRAND_ID, PRODUCT_ID, 4, 1, "2020-06-15T16:00:00", "2020-12-31T23:59:59");

        assertEquals(Optional.of(expectedPriceList), priceListOf(sut.findApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate)));
    }

    @Test
    void shouldIgnorePricesOfOtherBrandsAndProducts() {
        persist(2, PRODUCT_ID, 2, 9, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 1, 0, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, 35456L, 3, 9, "2020-06-14T00:00:00", "2020-12-31T23:59:59");

        assertEquals(Optional.of(1), priceListOf(sut.findApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)));
    }

    @Test
    void shouldReturnTheHighestPriorityWhenSeveralPricesOverlap() {
        persist(BRAND_ID, PRODUCT_ID, 1, 0, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 2, 2, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 3, 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59");

        assertEquals(Optional.of(2), priceListOf(sut.findApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)));
    }

    @Test
    void shouldBreakPriorityTiesByTheMostRecentStartDate() {
        persist(BRAND_ID, PRODUCT_ID, 1, 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 2, 1, "2020-06-14T12:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 3, 1, "2020-06-14T08:00:00", "2020-12-31T23:59:59");

        assertEquals(Optional.of(2), priceListOf(sut.findApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)));
    }

    @Test
    void shouldReturnTheLastInsertedPriceWhenPriorityAndStartDateAreTied() {
        persist(BRAND_ID, PRODUCT_ID, 1, 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 2, 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        persist(BRAND_ID, PRODUCT_ID, 3, 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59");

        assertEquals(Optional.of(3), priceListOf(sut.findApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)));
    }

    @ParameterizedTest(name = "[{0} - {1}] -> aplica: {2}")
    @CsvSource({
            "2020-06-14T16:00:00, 2020-06-14T18:00:00, true",
            "2020-06-14T10:00:00, 2020-06-14T16:00:00, true",
            "2020-06-14T16:00:01, 2020-06-14T20:00:00, false",
            "2020-06-14T10:00:00, 2020-06-14T15:59:59, false"
    })
    void shouldOnlyFindPricesWhosePeriodContainsTheDateIncludingBoundaries(String start, String end, boolean applicable) {
        persist(BRAND_ID, PRODUCT_ID, 1, 0, start, end);

        assertEquals(applicable, sut.findApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE).isPresent());
    }

    private void persist(int brandId, long productId, int priceList, int priority, String start, String end) {
        entityManager.persistAndFlush(PriceEntity.builder()
                .brandId(brandId)
                .productId(productId)
                .priceList(priceList)
                .startDate(LocalDateTime.parse(start))
                .endDate(LocalDateTime.parse(end))
                .priority(priority)
                .price(new BigDecimal("10.00"))
                .currency("EUR")
                .build());
    }

    private static Optional<Integer> priceListOf(Optional<Price> price) {
        return price.map(Price::priceList);
    }
}