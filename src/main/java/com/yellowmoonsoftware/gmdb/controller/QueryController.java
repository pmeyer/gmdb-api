package com.yellowmoonsoftware.gmdb.controller;

import com.yellowmoonsoftware.gmdb.dto.input.AlbumSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.ArtistSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.PubSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.SongSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.output.*;
import com.yellowmoonsoftware.gmdb.mappers.GMDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.yellowmoonsoftware.gmdb.util.ReactiveUtils.async;

@Controller
@RequiredArgsConstructor
public class QueryController {

    private final GMDBMapper gmdbMapper;

    @QueryMapping
    public Mono<List<SongSearchResult>> songSearch(@Argument final SongSearchCriteria criteria) {
        return async(() -> gmdbMapper.songSearch(criteria));
    }

    @QueryMapping
    public Mono<List<ArtistSearchResult>> artistSearch(@Argument final ArtistSearchCriteria criteria) {
        return async(() -> gmdbMapper.artistSearch(criteria));
    }

    @QueryMapping
    public Mono<List<PubSearchResult>> pubSearch(@Argument final PubSearchCriteria criteria) {
        return async(() -> gmdbMapper.pubSearch(criteria));
    }

    @QueryMapping
    public Mono<List<AlbumSearchResult>> albumSearch(@Argument final AlbumSearchCriteria criteria) {
        return async(() -> gmdbMapper.albumSearch(criteria));
    }

    @QueryMapping
    public Mono<List<Transcriber>> transcriberSearch(@Argument final String searchName) {
        return async(() -> gmdbMapper.getTranscribers(searchName));
    }
}
