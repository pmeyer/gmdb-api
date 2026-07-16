package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public JsonMapper createMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }

    @Bean
    public JacksonJsonDecoder jacksonJsonDecoder(final JsonMapper objectMapper) {
        return new JacksonJsonDecoder(objectMapper);
    }
}
