package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.out.PriceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class PriceRepositoryH2Adapter implements PriceRepository {
    private final JpaPriceRepository jpaPriceRepository;
    private final PriceEntityMapper priceEntityMapper;

    @Override
    public List<Price> findCandidates(Integer brandId, Long productId, LocalDateTime applicationDate) {
        List<PriceEntity> candidates = jpaPriceRepository.findCandidates(brandId, productId, applicationDate);
        return candidates.stream().map(priceEntityMapper::toDomain).toList();
    }
}
