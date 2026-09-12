package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.ApplicationPeriod;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Money;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import org.springframework.stereotype.Component;

@Component
public class PriceEntityMapper {
    public Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                buildApplicationPeriod(entity),
                entity.getPriority(),
                buildMoney(entity)
        );
    }

    private ApplicationPeriod buildApplicationPeriod(PriceEntity entity) {
        return new ApplicationPeriod(entity.getStartDate(), entity.getEndDate());
    }

    private Money buildMoney(PriceEntity entity) {
        return new Money(entity.getPrice(), entity.getCurrency());
    }
}
