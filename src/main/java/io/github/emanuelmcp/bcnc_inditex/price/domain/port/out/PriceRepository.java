package io.github.emanuelmcp.bcnc_inditex.price.domain.port.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository {
    List<Price> findCandidates(Integer brandId, Long productId, LocalDateTime applicationDate);
}
