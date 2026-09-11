package io.github.emanuelmcp.bcnc_inditex.price.domain.service;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PriceResolver {
    public Optional<Price> resolveApplicablePrice(LocalDateTime applicationDate, List<Price> candidates) {
        Objects.requireNonNull(applicationDate, "ApplicationDate can not be null");
        Objects.requireNonNull(candidates, "Candidates can not be null");

        return candidates.stream()
                .filter(price -> price.isApplicableOn(applicationDate))
                .max(Price.byApplicationPriority());
    }
}
