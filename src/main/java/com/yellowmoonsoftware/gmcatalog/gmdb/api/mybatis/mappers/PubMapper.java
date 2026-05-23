package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;

@Mapper
public interface PubMapper {
    Mono<Long> getPubId(@Param("id") final Long id);
}
