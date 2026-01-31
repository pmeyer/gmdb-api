package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.AbstractResourceDetailsConverter;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

import java.util.List;

@Getter
@Accessors(fluent = true)
public final class TranscriptionInput extends AbstractResourceDetailsConverter<TranscriptionDetails> {
    @Valid @NotNull
    private final SongInput song;
    @NotNull
    private final Integer pageNumber;
    private final FilePart file;
    private final List<@Valid TranscriberInput> transcribers;

    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    @Accessors(fluent = false)
    private final TranscriptionDetails details = new TranscriptionDetails(pageNumber);

    public TranscriptionInput(
            @Valid @NotNull SongInput song,
            @NotNull Integer pageNumber,
            @NotNull FilePart file,
            List<@Valid TranscriberInput> transcribers
    ) {
        super(ResourceSlug.TRANSCRIPTION, file);
        this.song = song;
        this.pageNumber = pageNumber;
        this.file = file;
        this.transcribers = transcribers;
    }
}
