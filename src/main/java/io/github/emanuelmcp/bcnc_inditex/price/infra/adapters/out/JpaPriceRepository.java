package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaPriceRepository extends JpaRepository<PriceEntity, Long> {
    @Query("""
            SELECT p FROM PriceEntity p
            WHERE p.brandId = :brandId
              AND p.productId = :productId
              AND p.startDate <= :applicationDate
              AND p.endDate >= :applicationDate
            ORDER BY p.priority DESC, p.startDate DESC, p.id DESC
            LIMIT 1
            """)
    Optional<PriceEntity> findApplicablePrice(
            @Param("brandId") Integer brandId,
            @Param("productId") Long productId,
            @Param("applicationDate") LocalDateTime applicationDate
    );
}