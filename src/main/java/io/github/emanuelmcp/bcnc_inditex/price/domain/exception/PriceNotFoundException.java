package io.github.emanuelmcp.bcnc_inditex.price.domain.exception;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(Long productId, Integer brandId, String applicationDate) {
        super(
                String.format(
                        "No applicable price found for product %d, brand %d at date %s",
                        productId, brandId, applicationDate
                )
        );
    }
}
