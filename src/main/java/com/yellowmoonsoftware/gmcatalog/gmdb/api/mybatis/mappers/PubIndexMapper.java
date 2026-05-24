package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper
public interface PubIndexMapper {
    Mono<Long> getPubIndexId(@Param("id") final Long id);
    Mono<PubIndexOut> getPubIndex(@Param("pubIdxId") final Long pubIdxId);
    Mono<PubIndexOut> upsertPubIndex(@Param("pubIdx") final PubIndexInput pubIdx);
    Flux<PubIndexOut> getPubIndices(@Param("criteria") final PubIndexCriteria criteria);
}
