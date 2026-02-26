package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;

import java.time.LocalDate;
import java.util.Objects;

public record SongAlbum(
        Long id,
        String title,
        Integer trackNumber,
        LocalDate releaseDate,
        String albumArtUrl,
        Long primaryArtistId
) {

    public SongAlbum(AlbumOut album, SongSearchResult song) {
        this(album.id(), album.title(), song.trackNumber(),
                album.details().releaseDate(), album.details().albumArtUrl(), album.primaryArtistId());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SongAlbum other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
