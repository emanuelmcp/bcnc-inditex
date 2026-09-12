package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in.dto;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import org.springframework.stereotype.Component;

@Component
public class PriceResponseMapper {
    public PriceResponseDto toResponse(Price price) {
        return new PriceResponseDto(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.applicationPeriod().start(),
                price.applicationPeriod().end(),
                price.money().amount(),
                price.money().currency()
        );
    }
}
