package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.ArtistSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.SongSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.AlbumMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.ArtistMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.SongMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriberMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.PublicationIndexService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryControllerTest {

    @Mock
    private SongMapper songMapper;

    @Mock
    private PubMapper pubMapper;

    @Mock
    private TranscriberMapper transcriberMapper;

    @Mock
    private PublicationIndexService publicationIndexService;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private QueryController queryController;

    @Test
    void songSearchDelegatesToMapper() {
        final SongSearchCriteria criteria = new SongSearchCriteria("song", null, null, null, null, null, null);
        final SongSearchResult result = new SongSearchResult(1L, "Song", 3, 4L);
        when(songMapper.songSearch(criteria)).thenReturn(Flux.just(result));

        StepVerifier.create(queryController.songSearch(criteria)).expectNext(result).verifyComplete();

        verify(songMapper).songSearch(criteria);
    }

    @Test
    void artistSearchDelegatesToMapper() {
        final ArtistSearchCriteria criteria = new ArtistSearchCriteria("alice", ArtistType.PERSON, null, null, null);
        final ArtistSearchResult result = new ArtistSearchResult(1L, "Alice", ArtistType.PERSON, java.util.Set.of());
        when(artistMapper.artistSearch(criteria)).thenReturn(Flux.just(result));

        StepVerifier.create(queryController.artistSearch(criteria)).expectNext(result).verifyComplete();

        verify(artistMapper).artistSearch(criteria);
    }

    @Test
    void pubSearchDelegatesToMapper() {
        final PubSearchCriteria criteria = new PubSearchCriteria(1L, 2L, "guide", PubType.BOOK, null, null, true);
        final PubSearchResult result = new PubSearchResult(1L, "Guide", PubType.BOOK, new com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails("First"), null, "ISBN-1", 2L);
        when(pubMapper.pubSearch(criteria)).thenReturn(Flux.just(result));

        StepVerifier.create(queryController.pubSearch(criteria)).expectNext(result).verifyComplete();

        verify(pubMapper).pubSearch(criteria);
    }

    @Test
    void albumSearchMapsAlbumOutToSearchResult() {
        final AlbumSearchCriteria criteria = new AlbumSearchCriteria("live", null, null, null);
        final AlbumDetails details = new AlbumDetails(LocalDate.of(2020, 4, 5));
        final AlbumOut album = new AlbumOut(1L, "Live Set", details, 2L, null);
        when(albumMapper.albumSearch(criteria)).thenReturn(Flux.just(album));

        StepVerifier.create(queryController.albumSearch(criteria))
            .assertNext(result -> {
                assertThat(result.id()).isEqualTo(1L);
                assertThat(result.title()).isEqualTo("Live Set");
                assertThat(result.releaseDate()).isEqualTo(LocalDate.of(2020, 4, 5));
                assertThat(result.albumArtUrl()).isNull();
                assertThat(result.primaryArtistId()).isEqualTo(2L);
            })
            .verifyComplete();

        verify(albumMapper).albumSearch(criteria);
    }

    @Test
    void transcriberSearchDelegatesToMapper() {
        final Transcriber result = new Transcriber(1L, "Alice");
        when(transcriberMapper.getTranscribers("alice")).thenReturn(Flux.just(result));

        StepVerifier.create(queryController.transcriberSearch("alice")).expectNext(result).verifyComplete();

        verify(transcriberMapper).getTranscribers("alice");
    }

    @Test
    void getPubIndicesDelegatesToService() {
        final PubIndexCriteria criteria = new PubIndexCriteria(PubType.BOOK);
        final PubIndexOut result = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(publicationIndexService.getPublicationIndices(criteria)).thenReturn(Flux.just(result));

        StepVerifier.create(queryController.getPubIndices(criteria)).expectNext(result).verifyComplete();

        verify(publicationIndexService).getPublicationIndices(criteria);
    }
}
