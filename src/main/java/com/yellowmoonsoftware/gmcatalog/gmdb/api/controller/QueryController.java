package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.GMDBMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.PublicationIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class QueryController {

    private final GMDBMapper gmdbMapper;
    private final PublicationIndexService publicationIndexService;

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

    @QueryMapping
    public Flux<PubIndexOut> getPubIndices(@Argument final PubIndexCriteria criteria) {
        return publicationIndexService.getPublicationIndices(criteria);
    }
}
