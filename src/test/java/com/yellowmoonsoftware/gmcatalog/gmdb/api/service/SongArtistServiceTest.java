package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongArtistInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongArtistServiceTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private SongArtistService songArtistService;

    @Test
    @SuppressWarnings("unchecked")
    void addSongArtistsBuildsReferenceAndUpsertedArtistInputs() {
        SongArtistInput reference = new SongArtistInput(10L, null, Set.of(SongArtistRole.WORDS_BY));
        SongArtistInput data = new SongArtistInput(null, new ArtistData("Alice", ArtistType.PERSON), Set.of(SongArtistRole.MUSIC_BY));
        when(artistService.upsertArtist(new ArtistInput(null, data.data())))
            .thenReturn(Mono.just(new ArtistOut(20L, "Alice", ArtistType.PERSON, null)));
        when(artistService.upsertSongArtists(org.mockito.ArgumentMatchers.anyList())).thenReturn(Flux.empty());

        StepVerifier.create(songArtistService.addSongArtists(1L, List.of(reference, data)))
            .verifyComplete();

        ArgumentCaptor<List<SongArtistIn>> captor = ArgumentCaptor.forClass(List.class);
        verify(artistService).upsertArtist(new ArtistInput(null, data.data()));
        verify(artistService).upsertSongArtists(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).songId()).isEqualTo(1L);
        assertThat(captor.getValue().get(0).artistId()).isEqualTo(10L);
        assertThat(captor.getValue().get(0).roles()).containsExactly(SongArtistRole.WORDS_BY);
        assertThat(captor.getValue().get(1).songId()).isEqualTo(1L);
        assertThat(captor.getValue().get(1).artistId()).isEqualTo(20L);
        assertThat(captor.getValue().get(1).roles()).containsExactly(SongArtistRole.MUSIC_BY);
        verifyNoMoreInteractions(artistService);
    }

    @Test
    void addSongArtistsTreatsNullListAsEmpty() {
        when(artistService.upsertSongArtists(List.of())).thenReturn(Flux.empty());

        StepVerifier.create(songArtistService.addSongArtists(1L, null))
            .verifyComplete();

        verify(artistService).upsertSongArtists(List.of());
        verifyNoMoreInteractions(artistService);
    }
}
