package io.github.emanuelmcp.bcnc_inditex.price.domain.port.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepository {
    Optional<Price> findApplicablePrice(Integer brandId, Long productId, LocalDateTime applicationDate);
}