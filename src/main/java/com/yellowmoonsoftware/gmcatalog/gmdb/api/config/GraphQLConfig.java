package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    RuntimeWiringConfigurer configure() {
        return builder -> builder
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.Json);
    }
}
