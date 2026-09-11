package io.github.emanuelmcp.bcnc_inditex.price.domain.port.in;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;

public interface FindApplicablePriceUseCase {
    Price findApplicablePrice(FindApplicablePriceQuery query);
}
