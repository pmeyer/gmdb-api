package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.AlbumMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private FileService fileService;

    @Mock
    private ArtistService artistService;

    @Mock
    private FilePart coverArt;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void upsertAlbumLoadsExistingAlbumForReferenceInput() {
        AlbumInput input = new AlbumInput(1L, null);
        AlbumOut output = albumOut(1L);
        when(albumMapper.getAlbumById(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(albumService.upsertAlbum(input))
            .expectNext(output)
            .verifyComplete();

        verify(albumMapper).getAlbumById(1L);
        verifyNoMoreInteractions(albumMapper);
        verifyNoInteractions(fileService, artistService);
    }

    @Test
    void upsertAlbumUpsertsPrimaryArtistAndStoresCoverArt() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(coverArt.filename()).thenReturn("cover.jpg");
        when(coverArt.headers()).thenReturn(headers);
        ArtistInput primaryArtist = new ArtistInput(null, new ArtistData("Alice", ArtistType.PERSON));
        AlbumInput input = new AlbumInput(null, new AlbumData("Live Set", coverArt, LocalDate.of(2020, 4, 5), primaryArtist));
        AlbumOut output = albumOut(1L);
        when(artistService.upsertArtist(primaryArtist)).thenReturn(Mono.just(new ArtistOut(20L, "Alice", ArtistType.PERSON, null)));
        when(albumMapper.upsertAlbum(any(AlbumIn.class))).thenReturn(Mono.just(output));
        when(fileService.put(coverArt, ResourceSlug.ALBUM_ART, Map.of("id", output.details().resourceId())))
            .thenReturn(Mono.just(new ResourceReference(ResourceSlug.ALBUM_ART, "album/1", "cover.jpg")));

        StepVerifier.create(albumService.upsertAlbum(input))
            .expectNext(output)
            .verifyComplete();

        ArgumentCaptor<AlbumIn> captor = ArgumentCaptor.forClass(AlbumIn.class);
        verify(artistService).upsertArtist(primaryArtist);
        verify(albumMapper).upsertAlbum(captor.capture());
        assertThat(captor.getValue().title()).isEqualTo("Live Set");
        assertThat(captor.getValue().primaryArtistId()).isEqualTo(20L);
        assertThat(captor.getValue().details().releaseDate()).isEqualTo(LocalDate.of(2020, 4, 5));
        verify(fileService).put(coverArt, ResourceSlug.ALBUM_ART, Map.of("id", output.details().resourceId()));
        verifyNoMoreInteractions(albumMapper, fileService, artistService);
    }

    @Test
    void upsertAlbumAllowsMissingPrimaryArtistAndCoverArt() {
        AlbumInput input = new AlbumInput(null, new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), null));
        AlbumOut output = albumOut(1L);
        when(albumMapper.upsertAlbum(any(AlbumIn.class))).thenReturn(Mono.just(output));

        StepVerifier.create(albumService.upsertAlbum(input))
            .expectNext(output)
            .verifyComplete();

        ArgumentCaptor<AlbumIn> captor = ArgumentCaptor.forClass(AlbumIn.class);
        verify(albumMapper).upsertAlbum(captor.capture());
        assertThat(captor.getValue().primaryArtistId()).isNull();
        verifyNoInteractions(fileService, artistService);
        verifyNoMoreInteractions(albumMapper);
    }

    @Test
    void upsertAlbumAllowsPrimaryArtistServiceToReturnEmpty() {
        ArtistInput primaryArtist = new ArtistInput(20L, null);
        AlbumInput input = new AlbumInput(null, new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), primaryArtist));
        AlbumOut output = albumOut(1L);
        when(artistService.upsertArtist(primaryArtist)).thenReturn(Mono.empty());
        when(albumMapper.upsertAlbum(any(AlbumIn.class))).thenReturn(Mono.just(output));

        StepVerifier.create(albumService.upsertAlbum(input))
            .expectNext(output)
            .verifyComplete();

        ArgumentCaptor<AlbumIn> captor = ArgumentCaptor.forClass(AlbumIn.class);
        verify(artistService).upsertArtist(primaryArtist);
        verify(albumMapper).upsertAlbum(captor.capture());
        assertThat(captor.getValue().primaryArtistId()).isNull();
        verifyNoInteractions(fileService);
        verifyNoMoreInteractions(albumMapper, artistService);
    }

    @Test
    void upsertAlbumPropagatesCoverArtStorageFailure() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(coverArt.filename()).thenReturn("cover.jpg");
        when(coverArt.headers()).thenReturn(headers);
        AlbumInput input = new AlbumInput(null, new AlbumData("Live Set", coverArt, LocalDate.of(2020, 4, 5), null));
        AlbumOut output = albumOut(1L);
        IllegalStateException failure = new IllegalStateException("storage failed");
        when(albumMapper.upsertAlbum(any(AlbumIn.class))).thenReturn(Mono.just(output));
        when(fileService.put(coverArt, ResourceSlug.ALBUM_ART, Map.of("id", output.details().resourceId())))
            .thenReturn(Mono.error(failure));

        StepVerifier.create(albumService.upsertAlbum(input))
            .expectErrorSatisfies(error -> assertThat(error).isSameAs(failure))
            .verify();

        verify(albumMapper).upsertAlbum(any(AlbumIn.class));
        verify(fileService).put(coverArt, ResourceSlug.ALBUM_ART, Map.of("id", output.details().resourceId()));
        verifyNoInteractions(artistService);
        verifyNoMoreInteractions(albumMapper, fileService);
    }

    private static AlbumOut albumOut(Long id) {
        AlbumDetails details = new AlbumDetails(LocalDate.of(2020, 4, 5)) {
            @Override
            public UUID resourceId() {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };
        return new AlbumOut(id, "Live Set", details, 20L, null);
    }
}
