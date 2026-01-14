package com.yellowmoonsoftware.gmdb.config;

import com.yellowmoonsoftware.gmdb.mybatis.r2dbc.PGCustomTypeHandlerConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.spring.annotation.R2dbcMapperScan;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.spring.support.R2dbcMybatisConfigurationCustomizer;

@Slf4j
@R2dbcMapperScan({"com.yellowmoonsoftware.gmdb.mybatis.mappers"})
@Configuration
public class MybatisConfiguration {

    @Bean
    public R2dbcMybatisConfigurationCustomizer r2dbcMybatisConfigurationCustomizer(final PGCustomTypeHandlerConverter pgCustomTypeHandlerConverter) {
        return configuration -> {
            log.info("R2DBC MyBatis adding type handler converters");
            configuration.addMybatisTypeHandlerConverter(pgCustomTypeHandlerConverter);
        };
    }
}
