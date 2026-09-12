package io.github.emanuelmcp.bcnc_inditex.price.domain.port.in;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record FindApplicablePriceQuery(Integer brandId, Long productId, LocalDateTime applicationDate) {
    public FindApplicablePriceQuery {
        Objects.requireNonNull(brandId, "BrandId can not be null");
        Objects.requireNonNull(productId, "ProductId can not be null");
        Objects.requireNonNull(applicationDate, "ApplicationDate can not be null");
        applicationDate = applicationDate.truncatedTo(ChronoUnit.SECONDS);
    }
}