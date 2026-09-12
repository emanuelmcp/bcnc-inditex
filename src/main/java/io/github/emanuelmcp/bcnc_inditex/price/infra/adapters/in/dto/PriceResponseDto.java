package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Price rate applicable to a product of a brand at a given point in time")
public record PriceResponseDto(
        @Schema(description = "Product identifier", example = "35455")
        Long productId,

        @Schema(description = "Brand identifier within the group (1 = ZARA)", example = "1")
        Integer brandId,

        @Schema(description = "Identifier of the applied price rate", example = "2")
        Integer priceList,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Start date from which the rate applies", example = "2020-06-14T15:00:00")
        LocalDateTime startDate,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "End date until which the rate applies", example = "2020-06-14T18:30:00")
        LocalDateTime endDate,

        @Schema(description = "Final sale price", example = "25.45")
        BigDecimal price,

        @Schema(description = "ISO currency code", example = "EUR")
        String currency
) {
}