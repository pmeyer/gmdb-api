package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class TranscriptionInOut extends UpsertResult{
    private final Long id;
    private final Long songId;
    private final Long pubId;
    private final TranscriptionDetails details;

    public TranscriptionInOut(final Long id, final Long songId, final Long pubId, final TranscriptionDetails details, final MergeAction action) {
        super(action);
        this.id = id;
        this.songId = songId;
        this.pubId = pubId;
        this.details = details;
    }

    public static TranscriptionInOut forNewTranscription(final Long songId, final Long pubId, final TranscriptionDetails details) {
        return new TranscriptionInOut(null, songId, pubId, details, null);
    }
}