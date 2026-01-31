package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.ConditionalNotNull;
import jakarta.validation.Valid;
import org.springframework.lang.NonNull;

import java.util.List;

@ConditionalNotNull(value = "id", ifNull = "data", message = "SongInput must have an ID or data")
public record SongInput(
        Long id,
        @Valid SongData data
) implements IdAndDataContainer<SongInput.SongData> {
    public record SongData(
            @NonNull String title,
            List<@Valid SongArtistInput> artists,
            @Valid AlbumTrackInput albumTrack) {
    }

    public record AlbumTrackInput(
            @NonNull Integer trackNumber,
            @Valid @NonNull AlbumInput album
    ) { }
}
