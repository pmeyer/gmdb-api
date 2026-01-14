package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@ToString(callSuper = true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class TranscriptionPublication extends PubSearchResult {
    @EqualsAndHashCode.Include
    private final Long transcriptionId;

    public TranscriptionPublication(Long id, String name, PubType type, PubDetails details, LocalDate pubDate, String serialNumber, Long transcriptionId) {
        super(id, name, type, details, pubDate, serialNumber);
        this.transcriptionId = transcriptionId;
    }
}
