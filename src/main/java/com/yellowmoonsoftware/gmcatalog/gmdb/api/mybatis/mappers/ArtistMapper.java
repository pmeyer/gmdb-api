package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.ArtistSearchResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Mapper
public interface ArtistMapper {
    Mono<Long> getArtistId(@Param("id") final Long id);
    Mono<ArtistOut> getArtistById(@Param("id") final Long id);
    Mono<ArtistOut> upsertArtist(@Param("input") final ArtistInput input);
    Flux<SongArtistOut> upsertSongArtists(@Param("songArtists") final List<SongArtistIn> songArtists);
    Flux<ArtistOut> getArtistsByIds(@Param("artistIds") final Set<Long> artistIds);
    Flux<ArtistSearchResult> artistSearch(@Param("criteria") final ArtistSearchCriteria criteria);
}
