package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString(callSuper = true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class TranscriptionTranscriber extends Transcriber {
    @EqualsAndHashCode.Include
    private final Long transcriptionId;

    public TranscriptionTranscriber(Long id, String name, Long transcriptionId) {
        super(id, name);
        this.transcriptionId = transcriptionId;
    }
}
