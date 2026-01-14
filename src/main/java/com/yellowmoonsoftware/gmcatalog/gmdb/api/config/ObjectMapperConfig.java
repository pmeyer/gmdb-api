package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    public Jackson2JsonDecoder jackson2JsonDecoder(final ObjectMapper objectMapper) {
        return new Jackson2JsonDecoder(objectMapper);
    }
}
