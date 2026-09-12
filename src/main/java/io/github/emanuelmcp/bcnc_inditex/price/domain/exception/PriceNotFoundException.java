package io.github.emanuelmcp.bcnc_inditex.price.domain.exception;

import java.time.LocalDateTime;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(Long productId, Integer brandId, LocalDateTime applicationDate) {
        super(
                String.format(
                        "No applicable price found for product %d, brand %d at date %s",
                        productId, brandId, applicationDate.toString()
                )
        );
    }
}
