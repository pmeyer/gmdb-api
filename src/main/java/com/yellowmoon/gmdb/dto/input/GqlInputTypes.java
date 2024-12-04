package com.yellowmoon.gmdb.dto.input;


import com.yellowmoon.gmdb.dto.ArtistType;
import com.yellowmoon.gmdb.dto.SongArtistRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GqlInputTypes {

    public record PubInput(
            Long id,
            LocalDate pubDate,
            @ConditionalNotNull(field = "id")
            PubIndexInput index,
            FilePart cover,
            Map<String, Object> details
    ) { }

    @Validated
    public record ArtistInput(
            Long id,
            @ConditionalNotNull(field = "id")
            String name,
            @ConditionalNotNull(field = "id")
            ArtistType type
    ) { }

    public record AlbumInput(
            Long id,
            @ConditionalNotNull(field = "id")
            String title,
            FilePart coverArt,
            LocalDate releaseDate,
            ArtistInput primaryArtist
    ) { }

    public record AlbumTrackInput(
            Integer trackNumber,
            AlbumInput album
    ) { }

    public record SongArtistInput(
            Long id,
            @ConditionalNotNull(field = "id")
            String name,
            @ConditionalNotNull(field = "id")
            ArtistType type,
            Set<SongArtistRole> roles
    ) { }

    public record SongInput(
            Long id,
            @ConditionalNotNull(field = "id")
            String title,
            List<SongArtistInput> artists,
            AlbumTrackInput albumTrack
    ) { }

    public record TranscriptionInput(
        SongInput song,
        PubInput pub,
        Integer pageNumber,
        FilePart file
    ) { }
}