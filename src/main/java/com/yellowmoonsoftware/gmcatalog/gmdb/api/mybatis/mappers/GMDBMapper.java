package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper
public interface GMDBMapper {
    Flux<Transcriber> getTranscribers(final String searchName);
    Flux<SongSearchResult> songSearch(@Param("criteria") final SongSearchCriteria criteria);
    Flux<ArtistSearchResult> artistSearch(@Param("criteria") final ArtistSearchCriteria criteria);
    Mono<PubSearchResult> getPub(@Param("pubId") final Long pubId);
    Flux<PubSearchResult> pubSearch(@Param("criteria") final PubSearchCriteria criteria);
    Mono<PubIndexOut> getPubIndex(@Param("pubIdxId") final Long pubIdxId);
}
