package io.github.emanuelmcp.bcnc_inditex.price.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record ApplicationPeriod(LocalDateTime start, LocalDateTime end) {
    public ApplicationPeriod {
        Objects.requireNonNull(start, "Start can not be null");
        Objects.requireNonNull(end, "End can not be null");
        if(start.isAfter(end)) {
            throw new IllegalArgumentException("Start can not be later than end");
        }
    }
}
