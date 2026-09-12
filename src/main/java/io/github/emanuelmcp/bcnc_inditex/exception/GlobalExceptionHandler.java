package io.github.emanuelmcp.bcnc_inditex.exception;

import io.github.emanuelmcp.bcnc_inditex.price.domain.exception.PriceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<UnifiedErrorResponseDto> handlePriceNotFoundException(PriceNotFoundException ex, HttpServletRequest request) {
        return buildUnifiedErrorResponseDto(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<UnifiedErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Parameter '%s' is invalid: expected a value of type %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid");
        return buildUnifiedErrorResponseDto(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<UnifiedErrorResponseDto> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildUnifiedErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UnifiedErrorResponseDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return buildUnifiedErrorResponseDto(HttpStatus.BAD_REQUEST, "Invalid input parameters", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnifiedErrorResponseDto> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildUnifiedErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    private ResponseEntity<UnifiedErrorResponseDto> buildUnifiedErrorResponseDto(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(
                new UnifiedErrorResponseDto(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI()
                )
        );
    }
}
