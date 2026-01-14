package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.FileResourceHandler;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import java.util.Arrays;

@Configuration
public class FileResourceConfiguration implements WebFluxConfigurer {
    public static final String RESOURCE_PATH = "/resources";

    @Bean
    public RouterFunction<?> routerFunction(final FileResourceHandler handler) {
        return RouterFunctions.route()
                .path(RESOURCE_PATH, b -> {
                    Arrays.stream(ResourceSlug.values())
                            .forEach(s -> {
                                b.GET(s.getRouterPath(), handler::getFile);
                            });
                    })
                .build();
    }
}
