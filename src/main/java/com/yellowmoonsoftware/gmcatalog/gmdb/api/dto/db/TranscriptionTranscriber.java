package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class TranscriptionTranscriber extends UpsertResult {
    private final Long transcriptionId;
    private final Long transcriberId;

    public TranscriptionTranscriber(final Long transcriptionId, final Long transcriberId, final MergeAction action) {
        super(action);
        this.transcriptionId = transcriptionId;
        this.transcriberId = transcriberId;
    }

    public static TranscriptionTranscriber forInput(final Long transcriptionId, final Long transcriberId) {
        return new TranscriptionTranscriber(transcriptionId, transcriberId, null);
    }
}
