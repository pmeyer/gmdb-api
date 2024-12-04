package com.yellowmoon.gmdb.graphql.config;

import com.yellowmoon.gmdb.graphql.UploadScalar;
import com.yellowmoon.gmdb.graphql.multipartmapper.GraphQlMultipartWebHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@Configuration
public class GraphQlMultipartFileUploadConfig {

    @Bean
    @ConditionalOnMissingBean(name = "graphQlMultipartUploadConfigurer")
    public RuntimeWiringConfigurer graphQlMultipartUploadConfigurer() {
        return builder -> builder.scalar(UploadScalar.INSTANCE);
    }

    @Bean
    @ConditionalOnMissingBean(name = "graphQlMultipartRouter")
    @Order(-10)
    public RouterFunction<ServerResponse> graphQlMultipartRouter(final GraphQlProperties properties, final GraphQlMultipartWebHandler graphQlMultipartWebHandler) {
        return  RouterFunctions.route()
                .POST(properties.getPath(),
                        RequestPredicates
                                .contentType(MULTIPART_FORM_DATA)
                                .and(RequestPredicates.accept(GraphQlMultipartWebHandler.SUPPORTED_MEDIA_TYPES.toArray(new MediaType[]{}))),
                        graphQlMultipartWebHandler::handleGraphQlMultipartRequest)
                .build();
    }
}
