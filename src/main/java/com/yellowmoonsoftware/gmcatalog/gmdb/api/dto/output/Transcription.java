package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;

import java.util.Objects;

public record Transcription(
    Long id,
    String url,
    Integer pageNumber,
    Long songId,
    Long pubId
) {
    public static Transcription from(final TranscriptionInOut transcription) {
        return new Transcription(
                transcription.id(),
                transcription.details().transcriptionUrl(),
                transcription.details().pageNumber(),
                transcription.songId(),
                transcription.pubId());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transcription that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
