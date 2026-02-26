package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Mapper
public interface AlbumMapper {
    Mono<AlbumOut> upsertAlbum(@Param("input") final AlbumIn input);
    Mono<AlbumOut> getAlbumById(@Param("id") final Long id);
    Flux<AlbumOut> getAlbumsByIds(@Param("albumIds") final Set<Long> albumIds);
}
