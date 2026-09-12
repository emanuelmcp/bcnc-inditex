package io.github.emanuelmcp.bcnc_inditex.price.infra.config;

import io.github.emanuelmcp.bcnc_inditex.price.application.FindApplicablePriceService;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceUseCase;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;
import io.github.emanuelmcp.bcnc_inditex.price.domain.service.PriceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceConfiguration {
    @Bean
    public PriceResolver priceResolver() {
        return new PriceResolver();
    }

    @Bean
    public FindApplicablePriceUseCase findApplicablePriceService(
            PriceRepository priceRepository,
            PriceResolver priceResolver
    ) {
        return new FindApplicablePriceService(priceRepository, priceResolver);
    }
}
