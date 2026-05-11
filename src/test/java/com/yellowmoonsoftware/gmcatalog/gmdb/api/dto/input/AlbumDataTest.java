package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumDataTest {

    @Mock
    private FilePart coverArt;

    @Test
    void exposesAlbumFields() {
        final ArtistInput primaryArtist = primaryArtist();
        final LocalDate releaseDate = LocalDate.of(2020, 4, 5);

        final AlbumData data = new AlbumData("Live Set", coverArt, releaseDate, primaryArtist);

        assertThat(data.title()).isEqualTo("Live Set");
        assertThat(data.coverArt()).isSameAs(coverArt);
        assertThat(data.releaseDate()).isEqualTo(releaseDate);
        assertThat(data.primaryArtist()).isSameAs(primaryArtist);
    }

    @Test
    void convertsToDetailsWithAlbumArtResource() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(coverArt.filename()).thenReturn("art.jpg");
        when(coverArt.headers()).thenReturn(headers);
        final AlbumData data = new AlbumData("Live Set", coverArt, LocalDate.of(2020, 4, 5), primaryArtist());

        final AlbumDetails details = data.toDetails();

        assertThat(details.releaseDate()).isEqualTo(LocalDate.of(2020, 4, 5));
        assertThat(details.resources())
            .containsEntry(ResourceSlug.ALBUM_ART, new ResourceAttributes("art.jpg", MediaType.IMAGE_JPEG));
    }

    @Test
    void convertsToDetailsWithoutAlbumArtWhenCoverIsNull() {
        final AlbumData data = new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), primaryArtist());

        final AlbumDetails details = data.toDetails();

        assertThat(details.releaseDate()).isEqualTo(LocalDate.of(2020, 4, 5));
        assertThat(details.resources()).isEmpty();
    }

    private static ArtistInput primaryArtist() {
        return new ArtistInput(10L, new ArtistData("Alice", ArtistType.PERSON));
    }
}
