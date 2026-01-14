package com.yellowmoonsoftware.gmdb.mybatis.mappers;

import com.yellowmoonsoftware.gmdb.dto.input.AlbumSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.ArtistSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.PubSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.SongSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.output.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;

@Mapper
public interface GMDBMapper {
    Flux<Transcriber> getTranscribers(final String searchName);
    Flux<SongSearchResult> songSearch(@Param("criteria") final SongSearchCriteria criteria);
    Flux<ArtistSearchResult> artistSearch(@Param("criteria") final ArtistSearchCriteria criteria);
    Flux<PubSearchResult> pubSearch(@Param("criteria") final PubSearchCriteria criteria);
    Flux<AlbumSearchResult> albumSearch(@Param("criteria") final AlbumSearchCriteria criteria);
}
