package io.github.emanuelmcp.bcnc_inditex.price.infra.config;

import io.github.emanuelmcp.bcnc_inditex.price.application.FindApplicablePriceService;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceUseCase;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceConfiguration {
    @Bean
    public FindApplicablePriceUseCase findApplicablePriceService(PriceRepository priceRepository) {
        return new FindApplicablePriceService(priceRepository);
    }
}