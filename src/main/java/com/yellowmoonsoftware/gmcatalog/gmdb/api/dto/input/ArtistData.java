package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import jakarta.validation.constraints.NotNull;

public record ArtistData(
        @NotNull String name,
        @NotNull ArtistType type
) {
}
