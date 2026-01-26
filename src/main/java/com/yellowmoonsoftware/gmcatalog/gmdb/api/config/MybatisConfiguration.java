package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type.JsonTypeHandlerRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.spring.annotation.R2dbcMapperScan;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.spring.support.R2dbcMybatisConfigurationCustomizer;

@Slf4j
@R2dbcMapperScan({"com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers"})
@Configuration
public class MybatisConfiguration {

    @Bean
    public R2dbcMybatisConfigurationCustomizer r2dbcMybatisConfigurationCustomizer(final JsonTypeHandlerRegistrar registrar) {
        return configuration -> {
            registrar.withConfig(configuration)
                    .register("com.yellowmoonsoftware.gmcatalog.gmdb.api.dto");
        };
    }
}
