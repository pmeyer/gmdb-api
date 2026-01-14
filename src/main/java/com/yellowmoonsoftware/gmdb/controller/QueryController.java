package com.yellowmoonsoftware.gmdb.controller;

import com.yellowmoonsoftware.gmdb.dto.input.AlbumSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.ArtistSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.PubSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.SongSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.output.*;
import com.yellowmoonsoftware.gmdb.mybatis.mappers.GMDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class QueryController {

    private final GMDBMapper gmdbMapper;

    @QueryMapping
    public Flux<SongSearchResult> songSearch(@Argument final SongSearchCriteria criteria) {
        return gmdbMapper.songSearch(criteria);
    }

    @QueryMapping
    public Flux<ArtistSearchResult> artistSearch(@Argument final ArtistSearchCriteria criteria) {
        return gmdbMapper.artistSearch(criteria);
    }

    @QueryMapping
    public Flux<PubSearchResult> pubSearch(@Argument final PubSearchCriteria criteria) {
        return gmdbMapper.pubSearch(criteria);
    }

    @QueryMapping
    public Flux<AlbumSearchResult> albumSearch(@Argument final AlbumSearchCriteria criteria) {
        return gmdbMapper.albumSearch(criteria);
    }

    @QueryMapping
    public Flux<Transcriber> transcriberSearch(@Argument final String searchName) {
        return gmdbMapper.getTranscribers(searchName);
    }
}
