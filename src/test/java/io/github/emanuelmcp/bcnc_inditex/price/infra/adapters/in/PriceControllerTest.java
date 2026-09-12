package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerTest {
    private static final String URL = "/api/prices";
    private static final String PRODUCT_ID = "35455";
    private static final String BRAND_ID = "1";
    private static final String CURRENCY = "EUR";

    private static final int RATE_1_PRICE_LIST = 1;
    private static final double RATE_1_PRICE = 35.50;
    private static final String RATE_1_START = "2020-06-14T00:00:00";
    private static final String RATE_1_END = "2020-12-31T23:59:59";

    private static final int RATE_2_PRICE_LIST = 2;
    private static final double RATE_2_PRICE = 25.45;
    private static final String RATE_2_START = "2020-06-14T15:00:00";
    private static final String RATE_2_END = "2020-06-14T18:30:00";

    private static final int RATE_3_PRICE_LIST = 3;
    private static final double RATE_3_PRICE = 30.50;

    private static final int RATE_4_PRICE_LIST = 4;
    private static final double RATE_4_PRICE = 38.95;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldTestCase1ARequestOn14DayAt10() throws Exception {
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(35455)))
                .andExpect(jsonPath("$.brandId", is(1)))
                .andExpect(jsonPath("$.priceList", is(RATE_1_PRICE_LIST)))
                .andExpect(jsonPath("$.price", is(RATE_1_PRICE)))
                .andExpect(jsonPath("$.currency", is(CURRENCY)))
                .andExpect(jsonPath("$.startDate", is(RATE_1_START)))
                .andExpect(jsonPath("$.endDate", is(RATE_1_END)));
    }

    @Test
    void shouldTestCase2ARequestOn14DayAt16() throws Exception {
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList", is(RATE_2_PRICE_LIST)))
                .andExpect(jsonPath("$.price", is(RATE_2_PRICE)))
                .andExpect(jsonPath("$.currency", is(CURRENCY)))
                .andExpect(jsonPath("$.startDate", is(RATE_2_START)))
                .andExpect(jsonPath("$.endDate", is(RATE_2_END)));
    }

    @Test
    void shouldTestCase3ARequestOn14DayAt21() throws Exception {
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T21:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList", is(RATE_1_PRICE_LIST)))
                .andExpect(jsonPath("$.price", is(RATE_1_PRICE)));
    }

    @Test
    void shouldTestCase4ARequestOn15DayAt10() throws Exception {
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-15T10:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList", is(RATE_3_PRICE_LIST)))
                .andExpect(jsonPath("$.price", is(RATE_3_PRICE)));
    }

    @Test
    void shouldTestCase5ARequestOn16DayAt21() throws Exception {
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-16T21:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList", is(RATE_4_PRICE_LIST)))
                .andExpect(jsonPath("$.price", is(RATE_4_PRICE)));
    }

    @Test
    void shouldReturnNotFoundWhenNoRateIsApplicable() throws Exception {
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2019-01-01T10:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenARequiredParameterIsMissing() throws Exception {
        mockMvc.perform(get(URL)
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isBadRequest());
    }
}