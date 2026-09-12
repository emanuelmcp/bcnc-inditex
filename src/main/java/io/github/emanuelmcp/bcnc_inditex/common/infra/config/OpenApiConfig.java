package io.github.emanuelmcp.bcnc_inditex.common.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI pricesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("BCNC Inditex - Prices API")
                        .description(
                                """
                                REST service that resolves the applicable price rate \
                                for a product of a brand at a given date and time.
                                """
                        ).version("v1.0"));
    }
}
