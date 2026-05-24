package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.SongAlbum;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.SongArtist;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.SongSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.AlbumMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.ArtistMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriptionMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongControllerTest {

    @Mock
    private TranscriptionMapper transcriptionMapper;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private SharedDataResolvers sharedDataResolvers;

    @InjectMocks
    private SongController songController;

    @Test
    void albumForSongMapsAlbumsToSongsAndSkipsSongsWithoutAlbumId() {
        final SongSearchResult withAlbum = new SongSearchResult(1L, "Opener", 3, 10L);
        final SongSearchResult withoutAlbum = new SongSearchResult(2L, "Solo", null, null);
        when(albumMapper.getAlbumsByIds(Set.of(10L))).thenReturn(Flux.just(albumOut()));

        StepVerifier.create(songController.albumForSong(Set.of(withAlbum, withoutAlbum)))
            .assertNext(result -> {
                assertThat(result).containsOnlyKeys(withAlbum);
                assertThat(result.get(withAlbum).id()).isEqualTo(10L);
                assertThat(result.get(withAlbum).trackNumber()).isEqualTo(3);
            })
            .verifyComplete();

        verify(albumMapper).getAlbumsByIds(Set.of(10L));
    }

    @Test
    void albumForSongSearchResultUsesCommonAlbumFetch() {
        final SongSearchResult song = new SongSearchResult(1L, "Opener", 3, 10L);
        when(albumMapper.getAlbumsByIds(Set.of(10L))).thenReturn(Flux.just(albumOut()));

        StepVerifier.create(songController.albumForSongSearchResult(Set.of(song)))
            .assertNext(result -> assertThat(result).containsKey(song))
            .verifyComplete();

        verify(albumMapper).getAlbumsByIds(Set.of(10L));
    }

    @Test
    void artistForSongAlbumDelegatesToSharedResolver() {
        final SongAlbum album = new SongAlbum(10L, "Live Set", 3, LocalDate.of(2020, 4, 5), null, 20L);
        final Map<SongAlbum, ArtistOut> output = Map.of(album, new ArtistOut(20L, "Alice", ArtistType.PERSON, null));
        when(sharedDataResolvers.artistsForAlbumArtistIdContainer(Set.of(album))).thenReturn(Mono.just(output));

        StepVerifier.create(songController.artistForSongAlbum(Set.of(album)))
            .expectNext(output)
            .verifyComplete();

        verify(sharedDataResolvers).artistsForAlbumArtistIdContainer(Set.of(album));
    }

    @Test
    void artistsForSongGroupsArtistsBySong() {
        final SongSearchResult song = new SongSearchResult(1L, "Opener", 3, 10L);
        final SongArtist artist = new SongArtist(20L, "Alice", ArtistType.PERSON, 1L, new SongArtistRole[]{SongArtistRole.WORDS_BY});
        when(artistMapper.getSongArtistBySongIds(Set.of(1L))).thenReturn(Flux.just(artist));

        StepVerifier.create(songController.artistsForSong(Set.of(song)))
            .assertNext(result -> assertThat(result).containsEntry(song, List.of(artist)))
            .verifyComplete();

        verify(artistMapper).getSongArtistBySongIds(Set.of(1L));
    }

    @Test
    void artistsForSongSearchResultUsesCommonArtistFetch() {
        final SongSearchResult song = new SongSearchResult(1L, "Opener", 3, 10L);
        final SongArtist artist = new SongArtist(20L, "Alice", ArtistType.PERSON, 1L, new SongArtistRole[]{SongArtistRole.WORDS_BY});
        when(artistMapper.getSongArtistBySongIds(Set.of(1L))).thenReturn(Flux.just(artist));

        StepVerifier.create(songController.artistsForSongSearchResult(Set.of(song)))
            .assertNext(result -> assertThat(result).containsEntry(song, List.of(artist)))
            .verifyComplete();

        verify(artistMapper).getSongArtistBySongIds(Set.of(1L));
    }

    @Test
    void transcriptionsGroupsBySong() {
        final SongSearchResult song = new SongSearchResult(1L, "Opener", 3, 10L);
        final TranscriptionInOut transcription = transcriptionInOut(30L, 1L, 40L);
        when(transcriptionMapper.getSongTranscriptionBySongIds(Set.of(1L))).thenReturn(Flux.just(transcription));

        StepVerifier.create(songController.transcriptions(Set.of(song)))
            .assertNext(result -> {
                assertThat(result).containsOnlyKeys(song);
                assertThat(result.get(song)).singleElement().satisfies(mapped -> {
                    assertThat(mapped.id()).isEqualTo(30L);
                    assertThat(mapped.url()).isEqualTo(transcription.details().transcriptionUrl());
                    assertThat(mapped.pageNumber()).isEqualTo(12);
                    assertThat(mapped.songId()).isEqualTo(1L);
                    assertThat(mapped.pubId()).isEqualTo(40L);
                });
            })
            .verifyComplete();

        verify(transcriptionMapper).getSongTranscriptionBySongIds(Set.of(1L));
    }

    private static AlbumOut albumOut() {
        return new AlbumOut(10L, "Live Set", new AlbumDetails(LocalDate.of(2020, 4, 5)), 20L, null);
    }

    private static TranscriptionInOut transcriptionInOut(final Long id, final Long songId, final Long pubId) {
        final TranscriptionDetails details = new TranscriptionDetails(12) {
            @Override
            public UUID resourceId() {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };
        details.resources().put(ResourceSlug.TRANSCRIPTION, new ResourceAttributes("page.pdf", MediaType.APPLICATION_PDF));
        return new TranscriptionInOut(id, songId, pubId, details, null);
    }
}
