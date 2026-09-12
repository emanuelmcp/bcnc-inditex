package io.github.emanuelmcp.bcnc_inditex.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
@RequiredArgsConstructor
public class ApiConfig implements WebMvcConfigurer {
    private final ApiProperties apiProperties;
    private static final String BASE_PACKAGE = "io.github.emanuelmcp.bcnc_inditex";
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
                apiProperties.prefix(),
                handlerType -> handlerType.isAnnotationPresent(RestController.class)
                        && handlerType.getPackageName().startsWith(BASE_PACKAGE)
        );
    }
}
