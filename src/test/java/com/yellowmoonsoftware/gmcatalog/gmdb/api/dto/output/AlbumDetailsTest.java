package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumDetailsTest {

    @Test
    void exposesReleaseDateAndInheritedResourceState() {
        final LocalDate releaseDate = LocalDate.of(1981, 2, 12);
        final AlbumDetails details = new AlbumDetails(releaseDate);

        assertThat(details.releaseDate()).isEqualTo(releaseDate);
        assertThat(details.resourceId()).isNull();
        assertThat(details.resources()).isEmpty();
    }

    @Test
    void albumArtUrlIsNullWhenNoAlbumArtResourceExists() {
        final AlbumDetails details = new AlbumDetails(LocalDate.of(1981, 2, 12));

        assertThat(details.albumArtUrl()).isNull();
    }
}
