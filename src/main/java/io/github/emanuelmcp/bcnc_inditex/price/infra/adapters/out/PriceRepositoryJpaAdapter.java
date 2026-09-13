package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PriceRepositoryJpaAdapter implements PriceRepository {
    private final JpaPriceRepository jpaPriceRepository;
    private final PriceEntityMapper priceEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Price> findApplicablePrice(Integer brandId, Long productId, LocalDateTime applicationDate) {
        return jpaPriceRepository.findApplicablePrice(brandId, productId, applicationDate)
                .map(priceEntityMapper::toDomain);
    }
}