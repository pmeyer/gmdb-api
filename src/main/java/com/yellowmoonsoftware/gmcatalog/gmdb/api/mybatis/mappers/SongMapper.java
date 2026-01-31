package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;

@Mapper
public interface SongMapper {
    Mono<SongOut> upsertSong(@Param("input") final SongIn input);
    Mono<SongOut> getSongById(@Param("id") final Long id);
}
