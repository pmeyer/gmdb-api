package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.ArtistMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SharedDataResolversTest {

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private SharedDataResolvers resolvers;

    @Test
    void artistsForAlbumArtistIdContainerMapsFetchedArtistsToContainers() {
        AlbumSearchResult first = new AlbumSearchResult(1L, "First", LocalDate.of(2020, 1, 1), null, 10L);
        AlbumSearchResult second = new AlbumSearchResult(2L, "Second", LocalDate.of(2021, 1, 1), null, 10L);
        AlbumSearchResult missingArtist = new AlbumSearchResult(3L, "Third", LocalDate.of(2022, 1, 1), null, null);
        ArtistOut artist = new ArtistOut(10L, "Alice", ArtistType.PERSON, null);
        when(artistMapper.getArtistsByIds(Set.of(10L))).thenReturn(Flux.just(artist));

        StepVerifier.create(resolvers.artistsForAlbumArtistIdContainer(Set.of(first, second, missingArtist)))
            .assertNext(result -> assertThat(result)
                .containsExactlyInAnyOrderEntriesOf(Map.of(first, artist, second, artist))
                .doesNotContainKey(missingArtist))
            .verifyComplete();

        verify(artistMapper).getArtistsByIds(Set.of(10L));
    }

    @Test
    void artistsForAlbumArtistIdContainerOmitsContainersWhenArtistIsNotFetched() {
        AlbumSearchResult album = new AlbumSearchResult(1L, "First", LocalDate.of(2020, 1, 1), null, 10L);
        when(artistMapper.getArtistsByIds(Set.of(10L))).thenReturn(Flux.empty());

        StepVerifier.create(resolvers.artistsForAlbumArtistIdContainer(Set.of(album)))
            .assertNext(result -> assertThat(result).isEmpty())
            .verifyComplete();

        verify(artistMapper).getArtistsByIds(Set.of(10L));
        verifyNoMoreInteractions(artistMapper);
    }

    @Test
    void artistsForAlbumArtistIdContainerHandlesOnlyNullArtistIds() {
        AlbumSearchResult missingArtist = new AlbumSearchResult(3L, "Third", LocalDate.of(2022, 1, 1), null, null);
        when(artistMapper.getArtistsByIds(Set.of())).thenReturn(Flux.empty());

        StepVerifier.create(resolvers.artistsForAlbumArtistIdContainer(Set.of(missingArtist)))
            .assertNext(result -> assertThat(result).isEmpty())
            .verifyComplete();

        verify(artistMapper).getArtistsByIds(Set.of());
        verifyNoMoreInteractions(artistMapper);
    }
}
