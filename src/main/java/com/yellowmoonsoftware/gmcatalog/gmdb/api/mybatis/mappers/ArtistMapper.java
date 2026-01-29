package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Mapper
public interface ArtistMapper {
    Mono<ArtistOut> getArtistById(@Param("id") final Long id);
    Mono<ArtistOut> upsertArtist(@Param("input") final ArtistInput input);
    Flux<SongArtistOut> upsertSongArtists(@Param("songArtists") final List<SongArtistIn> songArtists);
}
