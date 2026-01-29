package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;

@Mapper
public interface AlbumMapper {
    Mono<AlbumOut> upsertAlbum(@Param("input") final AlbumIn input);
    Mono<AlbumOut> getAlbumById(@Param("id") final Long id);
}
