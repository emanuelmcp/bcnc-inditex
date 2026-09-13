package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import java.util.Objects;

public record Price(
        Integer brandId,
        Long productId,
        Integer priceList,
        ApplicationPeriod applicationPeriod,
        Integer priority,
        Money money
) {
    public Price {
        Objects.requireNonNull(brandId, "BrandId can not be null");
        Objects.requireNonNull(productId, "ProductId can not be null");
        Objects.requireNonNull(priceList, "PriceList can not be null");
        Objects.requireNonNull(applicationPeriod, "ApplicationPeriod can not be null");
        Objects.requireNonNull(priority, "Priority can not be null");
        Objects.requireNonNull(money, "Money can not be null");
        if (priority < 0) {
            throw new IllegalArgumentException("Priority can not be negative");
        }
    }
}