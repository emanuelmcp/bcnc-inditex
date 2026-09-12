package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private static final int OTHER_PRODUCT_ID = 35456;
    private static final int OTHER_BRAND_ID = 2;
    private static final String CURRENCY = "EUR";

    private static final Rate RATE_1 =
            new Rate(PRODUCT_ID, BRAND_ID, 1, 35.50, "2020-06-14T00:00:00", "2020-12-31T23:59:59");
    private static final Rate RATE_2 =
            new Rate(PRODUCT_ID, BRAND_ID, 2, 25.45, "2020-06-14T15:00:00", "2020-06-14T18:30:00");
    private static final Rate RATE_3 =
            new Rate(PRODUCT_ID, BRAND_ID, 3, 30.50, "2020-06-15T00:00:00", "2020-06-15T11:00:00");
    private static final Rate RATE_4 =
            new Rate(PRODUCT_ID, BRAND_ID, 4, 38.95, "2020-06-15T16:00:00", "2020-12-31T23:59:59");

    private static final Rate OTHER_BRAND_RATE =
            new Rate(PRODUCT_ID, OTHER_BRAND_ID, 90, 11.11, "2020-01-01T00:00:00", "2020-12-31T23:59:59");
    private static final Rate OTHER_PRODUCT_RATE =
            new Rate(OTHER_PRODUCT_ID, BRAND_ID, 91, 22.22, "2020-01-01T00:00:00", "2020-12-31T23:59:59");

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
    @DisplayName("El último segundo del periodo de una tarifa todavía le pertenece")
    void shouldApplyRate4AtTheLastSecondOfItsPeriod() throws Exception {
        thenAppliedRateIs(whenQueryingPriceAt("2020-12-31T23:59:59"), RATE_4);
    }

    @Test
    @DisplayName("Solo se tienen en cuenta las tarifas de la cadena pedida")
    void shouldApplyTheRateOfTheRequestedBrand() throws Exception {
        thenAppliedRateIs(mockMvc.perform(priceRequest("2020-06-14T16:00:00", PRODUCT_ID, OTHER_BRAND_ID)), OTHER_BRAND_RATE);
    }

    @Test
    @DisplayName("Solo se tienen en cuenta las tarifas del producto pedido")
    void shouldApplyTheRateOfTheRequestedProduct() throws Exception {
        thenAppliedRateIs(mockMvc.perform(priceRequest("2020-06-14T16:00:00", OTHER_PRODUCT_ID, BRAND_ID)), OTHER_PRODUCT_RATE);
    }

    @ParameterizedTest(name = "producto {0}, cadena {1}")
    @CsvSource({"35455, 3", "99999, 1"})
    @DisplayName("404 cuando el producto no tiene tarifas en la cadena pedida")
    void shouldReturnNotFoundWhenProductHasNoRatesInTheRequestedBrand(int productId, int brandId) throws Exception {
        mockMvc.perform(priceRequest("2020-06-14T16:00:00", productId, brandId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
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

    @ParameterizedTest(name = "fecha {0}")
    @ValueSource(strings = {"2020-06-14T16:00:00", "2019-01-01T10:00:00"})
    @DisplayName("406 en JSON, y no 500, cuando el cliente solo acepta XML")
    void shouldReturnNotAcceptableInJsonWhenClientOnlyAcceptsXml(String applicationDate) throws Exception {
        mockMvc.perform(priceRequest(applicationDate, PRODUCT_ID, BRAND_ID).accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(406)));
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

    @ParameterizedTest(name = "fecha {0}")
    @ValueSource(strings = {"2020-06-14T16:00:00Z", "2020-06-14T16:00:00+05:00", "2020-06-14T16:00:00-03:00"})
    @DisplayName("400 cuando la fecha incluye zona horaria, en lugar de ignorarla")
    void shouldReturnBadRequestWhenDateIncludesTimeZone(String applicationDate) throws Exception {
        whenQueryingPriceAt(applicationDate)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("applicationDate")));
    }

    @ParameterizedTest(name = "fecha {0}")
    @ValueSource(strings = {"2020-06-14T16:00", "2020-06-14T16:00:00.5", "2020-06-14T16:00:00.123456789", "2020-12-31T23:59:59.500"})
    @DisplayName("400 cuando la fecha no sigue exactamente el formato yyyy-MM-dd'T'HH:mm:ss")
    void shouldReturnBadRequestWhenDateDoesNotMatchTheExactFormat(String applicationDate) throws Exception {
        whenQueryingPriceAt(applicationDate)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("applicationDate")));
    }

    @ParameterizedTest(name = "fecha {0}")
    @ValueSource(strings = {"2020-02-30T16:00:00", "2020-06-14T24:00:00"})
    @DisplayName("400 cuando la fecha tiene el formato correcto pero no existe")
    void shouldReturnBadRequestWhenDateDoesNotExist(String applicationDate) throws Exception {
        whenQueryingPriceAt(applicationDate)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("applicationDate")));
    }

    @Test
    @DisplayName("400 cuando el identificador de producto no es positivo")
    void shouldReturnBadRequestWhenProductIdIsNotPositive() throws Exception {
        mockMvc.perform(priceRequest("2020-06-14T10:00:00", -5, BRAND_ID))
                .andExpect(status().isBadRequest());
    }

    private ResultActions whenQueryingPriceAt(String applicationDate) throws Exception {
        return mockMvc.perform(priceRequest(applicationDate, PRODUCT_ID, BRAND_ID));
    }

    private static MockHttpServletRequestBuilder priceRequest(String applicationDate, int productId, int brandId) {
        return get(URL)
                .param("applicationDate", applicationDate)
                .param("productId", String.valueOf(productId))
                .param("brandId", String.valueOf(brandId));
    }

    private void thenAppliedRateIs(ResultActions result, Rate rate) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(rate.productId())))
                .andExpect(jsonPath("$.brandId", is(rate.brandId())))
                .andExpect(jsonPath("$.priceList", is(rate.priceList())))
                .andExpect(jsonPath("$.price", is(rate.price())))
                .andExpect(jsonPath("$.currency", is(CURRENCY)))
                .andExpect(jsonPath("$.startDate", is(rate.startDate())))
                .andExpect(jsonPath("$.endDate", is(rate.endDate())));
    }

    private record Rate(int productId, int brandId, int priceList, double price, String startDate, String endDate) {
    }
}