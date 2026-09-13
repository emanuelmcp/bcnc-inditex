package io.github.emanuelmcp.bcnc_inditex.price.application;

import io.github.emanuelmcp.bcnc_inditex.price.domain.exception.PriceNotFoundException;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceQuery;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceUseCase;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;


public class FindApplicablePriceService implements FindApplicablePriceUseCase {
    private final PriceRepository priceRepository;

    public FindApplicablePriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public Price findApplicablePrice(FindApplicablePriceQuery query) {
        return priceRepository.findApplicablePrice(query.brandId(), query.productId(), query.applicationDate())
                .orElseThrow(
                        () -> new PriceNotFoundException(
                                query.productId(),
                                query.brandId(),
                                query.applicationDate()
                        )
                );
    }
}