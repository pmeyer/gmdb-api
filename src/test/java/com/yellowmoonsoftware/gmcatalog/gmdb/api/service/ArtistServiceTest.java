package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.MergeAction;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.ArtistMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void upsertArtistDelegatesToUpsertForDataInput() {
        final ArtistInput input = new ArtistInput(null, new ArtistData("Alice", ArtistType.PERSON));
        final ArtistOut output = new ArtistOut(1L, "Alice", ArtistType.PERSON, MergeAction.INSERT);
        when(artistMapper.upsertArtist(input)).thenReturn(Mono.just(output));

        StepVerifier.create(artistService.upsertArtist(input))
            .expectNext(output)
            .verifyComplete();

        verify(artistMapper).upsertArtist(input);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void upsertArtistLoadsExistingArtistForReferenceInput() {
        final ArtistInput input = new ArtistInput(1L, null);
        final ArtistOut output = new ArtistOut(1L, "Alice", ArtistType.PERSON, null);
        when(artistMapper.getArtistId(1L)).thenReturn(Mono.just(1L));
        when(artistMapper.getArtistById(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(artistService.upsertArtist(input))
            .expectNext(output)
            .verifyComplete();

        verify(artistMapper).getArtistId(1L);
        verify(artistMapper).getArtistById(1L);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void upsertArtistRejectsUnknownReferenceId() {
        final ArtistInput input = new ArtistInput(1L, null);
        when(artistMapper.getArtistId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(artistService.upsertArtist(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown artist ID: 1");
                })
                .verify();

        verify(artistMapper).getArtistId(1L);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void upsertArtistValidatesExistingIdForIdAndDataInput() {
        final ArtistInput input = new ArtistInput(1L, new ArtistData("Alice", ArtistType.PERSON));
        final ArtistOut output = new ArtistOut(1L, "Alice", ArtistType.PERSON, MergeAction.UPDATE);
        when(artistMapper.getArtistId(1L)).thenReturn(Mono.just(1L));
        when(artistMapper.upsertArtist(input)).thenReturn(Mono.just(output));

        StepVerifier.create(artistService.upsertArtist(input))
                .expectNext(output)
                .verifyComplete();

        verify(artistMapper).getArtistId(1L);
        verify(artistMapper).upsertArtist(input);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void upsertArtistRejectsUnknownIdAndDataInputBeforeUpsert() {
        final ArtistInput input = new ArtistInput(1L, new ArtistData("Alice", ArtistType.PERSON));
        when(artistMapper.getArtistId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(artistService.upsertArtist(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown artist ID: 1");
                })
                .verify();

        verify(artistMapper).getArtistId(1L);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void validateArtistIdReturnsExistingId() {
        when(artistMapper.getArtistId(1L)).thenReturn(Mono.just(1L));

        StepVerifier.create(artistService.validateArtistId(1L))
                .expectNext(1L)
                .verifyComplete();

        verify(artistMapper).getArtistId(1L);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void validateArtistIdRejectsUnknownId() {
        when(artistMapper.getArtistId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(artistService.validateArtistId(1L))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown artist ID: 1");
                })
                .verify();

        verify(artistMapper).getArtistId(1L);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void upsertSongArtistsDelegatesToMapper() {
        final List<SongArtistIn> input = List.of(new SongArtistIn(1L, 2L, new SongArtistRole[]{SongArtistRole.WORDS_BY}));
        final SongArtistOut output = new SongArtistOut(1L, 2L, new SongArtistRole[]{SongArtistRole.WORDS_BY}, MergeAction.INSERT);
        when(artistMapper.upsertSongArtists(input)).thenReturn(Flux.just(output));

        StepVerifier.create(artistService.upsertSongArtists(input))
            .expectNext(output)
            .verifyComplete();

        verify(artistMapper).upsertSongArtists(input);
        verifyNoMoreInteractions(artistMapper);
    }
}
