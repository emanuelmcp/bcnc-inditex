package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in;

import io.github.emanuelmcp.bcnc_inditex.common.infra.exception.UnifiedErrorResponseDto;
import io.github.emanuelmcp.bcnc_inditex.price.domain.model.Price;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceQuery;
import io.github.emanuelmcp.bcnc_inditex.price.domain.port.in.FindApplicablePriceUseCase;
import io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in.dto.PriceResponseDto;
import io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in.dto.PriceResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/prices")
@Tag(name = "Prices", description = "Endpoints for querying applicable price rates")
@RequiredArgsConstructor
public class PriceController {
    private final FindApplicablePriceUseCase findApplicablePriceUseCase;
    private final PriceResponseMapper priceResponseMapper;


    @Operation(
            summary = "Find the applicable price rate",
            description = """
                    Resolves the single price rate that applies to a given product
                    brand and date. When several rates overlap for the same date,
                    the one with the highest priority is returned.
                   """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Applicable price rate found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PriceResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing or malformed request parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UnifiedErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No applicable price rate found for the given parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UnifiedErrorResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<PriceResponseDto> getApplicablePrice(
            @Parameter(description = "Date and time at which the price should apply",
                    example = "2020-06-14T16:00:00", required = true)
            @RequestParam("applicationDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime applicationDate,

            @Parameter(description = "Product identifier", example = "35455", required = true)
            @RequestParam("productId")
            @Positive(message = "productId must be positive") Long productId,

            @Parameter(description = "Brand identifier (1 = ZARA)", example = "1", required = true)
            @RequestParam("brandId")
            @Positive(message = "brandId must be positive") Integer brandId
    ) {
        FindApplicablePriceQuery query = new FindApplicablePriceQuery(brandId, productId, applicationDate);
        Price applicablePrice = findApplicablePriceUseCase.findApplicablePrice(query);
        return ResponseEntity.ok(priceResponseMapper.toResponse(applicablePrice));
    }
}
