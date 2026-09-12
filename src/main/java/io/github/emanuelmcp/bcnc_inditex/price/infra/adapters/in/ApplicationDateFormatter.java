package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in;

import org.jspecify.annotations.NonNull;
import org.springframework.format.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class ApplicationDateFormatter implements Formatter<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);

    @Override
    public LocalDateTime parse(@NonNull String text, @NonNull Locale locale) {
        return LocalDateTime.parse(text, FORMATTER);
    }

    @Override
    public String print(@NonNull LocalDateTime date, @NonNull Locale locale) {
        return FORMATTER.format(date);
    }
}