package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumSearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    @Mock
    private SharedDataResolvers sharedDataResolvers;

    @InjectMocks
    private AlbumController albumController;

    @Test
    void artistForAlbumDelegatesToSharedResolver() {
        final Set<AlbumSearchResult> albums = Set.of(new AlbumSearchResult(1L, "Live Set", LocalDate.of(2020, 4, 5), null, 2L));
        final Map<AlbumSearchResult, ArtistOut> output = Map.of();
        when(sharedDataResolvers.artistsForAlbumArtistIdContainer(albums)).thenReturn(Mono.just(output));

        StepVerifier.create(albumController.artistForAlbum(albums))
            .expectNext(output)
            .verifyComplete();

        verify(sharedDataResolvers).artistsForAlbumArtistIdContainer(albums);
    }
}
