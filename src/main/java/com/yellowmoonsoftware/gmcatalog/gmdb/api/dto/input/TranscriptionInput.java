package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;

public record TranscriptionInput(
        @Valid @NotNull SongInput song,
        @NotNull Integer pageNumber,
        @NonNull FilePart file
) {
}
