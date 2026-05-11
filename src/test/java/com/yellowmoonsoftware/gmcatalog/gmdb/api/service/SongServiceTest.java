package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.SongMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private AlbumService albumService;

    @Mock
    private SongArtistService songArtistService;

    @Mock
    private SongMapper songMapper;

    @InjectMocks
    private SongService songService;

    @Test
    void albumTrackRecordExposesValues() {
        final AlbumOut album = albumOut(2L);

        final SongService.AlbumTrack albumTrack = new SongService.AlbumTrack(album, 3);

        assertThat(albumTrack.album()).isSameAs(album);
        assertThat(albumTrack.trackNumber()).isEqualTo(3);
    }

    @Test
    void upsertSongLoadsExistingSongForReferenceInput() {
        final SongInput input = new SongInput(1L, null);
        final SongOut output = new SongOut(1L, "Opener", null, null, null);
        when(songMapper.getSongById(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(songService.upsertSong(input))
            .expectNext(output)
            .verifyComplete();

        verify(songMapper).getSongById(1L);
        verifyNoMoreInteractions(songMapper);
        verifyNoInteractions(albumService, songArtistService);
    }

    @Test
    void upsertSongUpsertsAlbumAndSongArtistsWhenAlbumTrackPresent() {
        final AlbumInput albumInput = albumInput();
        final SongInput.SongData data = new SongInput.SongData("Opener", List.of(), new SongInput.AlbumTrackInput(3, albumInput));
        final SongInput input = new SongInput(null, data);
        final SongOut output = new SongOut(10L, "Opener", new SongDetails(3), 2L, null);
        when(albumService.upsertAlbum(albumInput)).thenReturn(Mono.just(albumOut(2L)));
        when(songMapper.upsertSong(any(SongIn.class))).thenReturn(Mono.just(output));
        when(songArtistService.addSongArtists(10L, List.of())).thenReturn(Mono.empty());

        StepVerifier.create(songService.upsertSong(input))
            .expectNext(output)
            .verifyComplete();

        final ArgumentCaptor<SongIn> captor = ArgumentCaptor.forClass(SongIn.class);
        verify(albumService).upsertAlbum(albumInput);
        verify(songMapper).upsertSong(captor.capture());
        assertThat(captor.getValue().title()).isEqualTo("Opener");
        assertThat(captor.getValue().details().trackNumber()).isEqualTo(3);
        assertThat(captor.getValue().albumId()).isEqualTo(2L);
        verify(songArtistService).addSongArtists(10L, List.of());
        verifyNoMoreInteractions(albumService, songMapper, songArtistService);
    }

    @Test
    void upsertSongAllowsMissingAlbumTrack() {
        final SongInput.SongData data = new SongInput.SongData("Opener", List.of(), null);
        final SongInput input = new SongInput(null, data);
        final SongOut output = new SongOut(10L, "Opener", null, null, null);
        when(songMapper.upsertSong(any(SongIn.class))).thenReturn(Mono.just(output));
        when(songArtistService.addSongArtists(10L, List.of())).thenReturn(Mono.empty());

        StepVerifier.create(songService.upsertSong(input))
            .expectNext(output)
            .verifyComplete();

        final ArgumentCaptor<SongIn> captor = ArgumentCaptor.forClass(SongIn.class);
        verify(songMapper).upsertSong(captor.capture());
        assertThat(captor.getValue().details()).isNull();
        assertThat(captor.getValue().albumId()).isNull();
        verify(songArtistService).addSongArtists(10L, List.of());
        verifyNoInteractions(albumService);
        verifyNoMoreInteractions(songMapper, songArtistService);
    }

    private static AlbumInput albumInput() {
        final ArtistInput artist = new ArtistInput(20L, new ArtistData("Alice", ArtistType.PERSON));
        final AlbumData data = new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), artist);
        return new AlbumInput(2L, data);
    }

    private static AlbumOut albumOut(Long id) {
        return new AlbumOut(id, "Live Set", new AlbumDetails(LocalDate.of(2020, 4, 5)), 20L, null);
    }
}
