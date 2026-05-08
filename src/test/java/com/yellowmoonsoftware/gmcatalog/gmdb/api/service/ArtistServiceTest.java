package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.MergeAction;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
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
        ArtistInput input = new ArtistInput(null, new ArtistData("Alice", ArtistType.PERSON));
        ArtistOut output = new ArtistOut(1L, "Alice", ArtistType.PERSON, MergeAction.INSERT);
        when(artistMapper.upsertArtist(input)).thenReturn(Mono.just(output));

        StepVerifier.create(artistService.upsertArtist(input))
            .expectNext(output)
            .verifyComplete();

        verify(artistMapper).upsertArtist(input);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void upsertArtistLoadsExistingArtistForReferenceInput() {
        ArtistInput input = new ArtistInput(1L, null);
        ArtistOut output = new ArtistOut(1L, "Alice", ArtistType.PERSON, null);
        when(artistMapper.getArtistById(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(artistService.upsertArtist(input))
            .expectNext(output)
            .verifyComplete();

        verify(artistMapper).getArtistById(1L);
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void upsertSongArtistsDelegatesToMapper() {
        List<SongArtistIn> input = List.of(new SongArtistIn(1L, 2L, new SongArtistRole[]{SongArtistRole.WORDS_BY}));
        SongArtistOut output = new SongArtistOut(1L, 2L, new SongArtistRole[]{SongArtistRole.WORDS_BY}, MergeAction.INSERT);
        when(artistMapper.upsertSongArtists(input)).thenReturn(Flux.just(output));

        StepVerifier.create(artistService.upsertSongArtists(input))
            .expectNext(output)
            .verifyComplete();

        verify(artistMapper).upsertSongArtists(input);
        verifyNoMoreInteractions(artistMapper);
    }
}
