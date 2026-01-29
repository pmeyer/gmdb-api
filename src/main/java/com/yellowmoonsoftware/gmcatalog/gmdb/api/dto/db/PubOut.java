package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Accessors(fluent = true)
public class PubOut extends UpsertResult {
    private final Long id;
    private final LocalDate pubDate;
    private final Long pubIndexId;
    private final PubDetails details;

    public PubOut(final Long id, final LocalDate pubDate, final Long pubIndexId, final PubDetails details, final MergeAction action) {
        super(action);
        this.id = id;
        this.pubDate = pubDate;
        this.pubIndexId = pubIndexId;
        this.details = details;
    }
}
