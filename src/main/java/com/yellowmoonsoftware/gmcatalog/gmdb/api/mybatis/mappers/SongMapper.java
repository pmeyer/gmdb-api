package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.SongSearchResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Mapper
public interface SongMapper {
    Mono<SongOut> upsertSong(@Param("input") final SongIn input);
    Mono<Long> getSongId(@Param("id") final Long id);
    Mono<SongOut> getSongById(@Param("id") final Long id);
    Flux<SongSearchResult> songSearch(@Param("criteria") final SongSearchCriteria criteria);
    Flux<SongSearchResult> getSongsBySongIds(@Param("songIds") final Set<Long> songIds);
}
