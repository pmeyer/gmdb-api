package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import org.springframework.lang.NonNull;

public record ArtistData(
        @NonNull String name,
        @NonNull ArtistType type
) {
}
