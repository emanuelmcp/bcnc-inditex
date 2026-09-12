package io.github.emanuelmcp.bcnc_inditex.price.domain.exception;

import io.github.emanuelmcp.bcnc_inditex.common.domain.exception.ResourceNotFoundException;
import java.time.LocalDateTime;

public class PriceNotFoundException extends ResourceNotFoundException {
    public PriceNotFoundException(Long productId, Integer brandId, LocalDateTime applicationDate) {
        super(
                String.format(
                        "No applicable price found for product %d, brand %d at date %s",
                        productId, brandId, applicationDate.toString()
                )
        );
    }
}