package io.github.emanuelmcp.bcnc_inditex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "api")
public record ApiProperties(@DefaultValue("/api/v1") String prefix) {
}