package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.AbstractResourceDetailsConverter;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@Accessors(fluent = true)
public class AlbumData extends AbstractResourceDetailsConverter<AlbumDetails> {
    @NonNull
    private final String title;
    private final FilePart coverArt;
    private final LocalDate releaseDate;
    @Valid
    private final ArtistInput primaryArtist;

    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    @Accessors(fluent = false)
    private final AlbumDetails details = new AlbumDetails(releaseDate);

    public AlbumData(@NonNull String title, FilePart coverArt, LocalDate releaseDate, @Valid ArtistInput primaryArtist) {
        super(ResourceSlug.ALBUM_ART, coverArt);
        this.title = title;
        this.coverArt = coverArt;
        this.releaseDate = releaseDate;
        this.primaryArtist = primaryArtist;
    }
}
