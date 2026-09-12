package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GET /api/v1/prices - resolución de la tarifa aplicable")
class PriceControllerIntegrationTest {

    private static final String URL = "/api/v1/prices";
    private static final int PRODUCT_ID = 35455;
    private static final int BRAND_ID = 1;
    private static final String CURRENCY = "EUR";

    private static final Rate RATE_1 =
            new Rate(1, 35.50, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
    private static final Rate RATE_2 =
            new Rate(2, 25.45, "2020-06-14T15:00:00", "2020-06-14T18:30:00");
    private static final Rate RATE_3 =
            new Rate(3, 30.50, "2020-06-15T00:00:00", "2020-06-15T11:00:00");
    private static final Rate RATE_4 =
            new Rate(4, 38.95, "2020-06-15T16:00:00", "2020-12-31T23:59:59");

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: petición a las 10:00 del día 14 del producto 35455 para la brand 1 (ZARA)")
    void shouldApplyRate1WhenRequestedAt10OnJune14() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-14T10:00:00"), RATE_1);
    }

    @Test
    @DisplayName("Test 2: petición a las 16:00 del día 14 del producto 35455 para la brand 1 (ZARA)")
    void shouldApplyRate2WhenRequestedAt16OnJune14() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-14T16:00:00"), RATE_2);
    }

    @Test
    @DisplayName("Test 3: petición a las 21:00 del día 14 del producto 35455 para la brand 1 (ZARA)")
    void shouldApplyRate1WhenRequestedAt21OnJune14() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-14T21:00:00"), RATE_1);
    }

    @Test
    @DisplayName("Test 4: petición a las 10:00 del día 15 del producto 35455 para la brand 1 (ZARA)")
    void shouldApplyRate3WhenRequestedAt10OnJune15() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-15T10:00:00"), RATE_3);
    }

    @Test
    @DisplayName("Test 5: petición a las 21:00 del día 16 del producto 35455 para la brand 1 (ZARA)")
    void shouldApplyRate4WhenRequestedAt21OnJune16() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-16T21:00:00"), RATE_4);
    }

    @Test
    @DisplayName("El instante exacto de inicio de una tarifa ya le pertenece")
    void shouldApplyRate2AtTheExactStartOfItsPeriod() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-14T15:00:00"), RATE_2);
    }

    @Test
    @DisplayName("El instante exacto de fin de una tarifa todavía le pertenece")
    void shouldApplyRate2AtTheExactEndOfItsPeriod() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-14T18:30:00"), RATE_2);
    }

    @Test
    @DisplayName("Un segundo después de expirar la tarifa de mayor prioridad se vuelve a la base")
    void shouldFallBackToRate1OneSecondAfterRate2Expires() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-06-14T18:30:01"), RATE_1);
    }

    @Test
    @DisplayName("404 cuando ninguna tarifa aplica en la fecha consultada")
    void shouldReturnNotFoundWhenNoRateIsApplicable() throws Exception {
        whenQueryingPriceAt("2019-01-01T10:00:00")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.path", is(URL)));
    }

    @Test
    @DisplayName("404 cuando la ruta no existe, no 500")
    void shouldReturnNotFoundWhenRouteDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("405 cuando se usa un método distinto de GET, con cabecera Allow")
    void shouldReturnMethodNotAllowedWhenMethodIsNotGet() throws Exception {
        mockMvc.perform(post(URL)
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", containsString("GET")));
    }

    @Test
    @DisplayName("400 cuando falta un parámetro obligatorio")
    void shouldReturnBadRequestWhenARequiredParameterIsMissing() throws Exception {
        mockMvc.perform(get(URL)
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("400 cuando la fecha no tiene formato ISO")
    void shouldReturnBadRequestWhenDateIsMalformed() throws Exception {
        whenQueryingPriceAt("14-06-2020")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("applicationDate")));
    }

    @Test
    @DisplayName("400 cuando el identificador de producto no es positivo")
    void shouldReturnBadRequestWhenProductIdIsNotPositive() throws Exception {
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "-5")
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isBadRequest());
    }

    private ResultActions whenQueryingPriceAt(String applicationDate) throws Exception {
        return mockMvc.perform(get(URL)
                .param("applicationDate", applicationDate)
                .param("productId", String.valueOf(PRODUCT_ID))
                .param("brandId", String.valueOf(BRAND_ID)));
    }

    private void thenAppliedRateIs(ResultActions result, Rate rate) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(PRODUCT_ID)))
                .andExpect(jsonPath("$.brandId", is(BRAND_ID)))
                .andExpect(jsonPath("$.priceList", is(rate.priceList())))
                .andExpect(jsonPath("$.price", is(rate.price())))
                .andExpect(jsonPath("$.currency", is(CURRENCY)))
                .andExpect(jsonPath("$.startDate", is(rate.startDate())))
                .andExpect(jsonPath("$.endDate", is(rate.endDate())));
    }

    private record Rate(int priceList, double price, String startDate, String endDate) {
    }
}