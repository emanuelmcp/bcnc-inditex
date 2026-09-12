package io.github.emanuelmcp.bcnc_inditex.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard error payload returned for failed requests")
public record UnifiedErrorResponseDto(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Timestamp when the error occurred", example = "2026-09-12T10:15:30")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "HTTP status reason phrase", example = "Not Found")
        String error,

        @Schema(description = "Error message", example = "No applicable price found for product 35455, brand 1 at date 2019-01-01T10:00")
        String message,

        @Schema(description = "Request path that produced the error", example = "/api/prices")
        String path

) {

}
