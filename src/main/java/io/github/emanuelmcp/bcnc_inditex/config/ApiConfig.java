package io.github.emanuelmcp.bcnc_inditex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {
    private static final String API_PREFIX = "/api/v1";
    private static final String BASE_PACKAGE = "io.github.emanuelmcp.bcnc_inditex";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
                API_PREFIX,
                handlerType -> handlerType.isAnnotationPresent(RestController.class)
                        && handlerType.getPackageName().startsWith(BASE_PACKAGE)
        );
    }
}
