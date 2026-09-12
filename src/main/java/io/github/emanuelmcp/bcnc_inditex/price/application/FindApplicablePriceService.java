package io.github.emanuelmcp.bcnc_inditex.price.application;

import io.github.emanuelmcp.bcnc_inditex.price.domain.exception.PriceNotFoundException;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceQuery;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceUseCase;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;
import io.github.emanuelmcp.bcnc_inditex.price.domain.service.PriceResolver;

import java.util.List;


public class FindApplicablePriceService implements FindApplicablePriceUseCase {
    private final PriceRepository priceRepository;
    private final PriceResolver priceResolver;

    public FindApplicablePriceService(PriceRepository priceRepository, PriceResolver priceResolver) {
        this.priceRepository = priceRepository;
        this.priceResolver = priceResolver;
    }

    @Override
    public Price findApplicablePrice(FindApplicablePriceQuery query) {
        List<Price> candidates = priceRepository.findCandidates(query.brandId(), query.productId(), query.applicationDate());
        return priceResolver.resolveApplicablePrice(query.applicationDate(), candidates)
                .orElseThrow(
                        () -> new PriceNotFoundException(
                                query.productId(),
                                query.brandId(),
                                query.applicationDate()
                        )
                );
    }
}
