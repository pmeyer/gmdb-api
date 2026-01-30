package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class TranscriberOut extends UpsertResult {
    private final Long id;
    private final String name;

    public TranscriberOut(final Long id, final String name, final MergeAction action) {
        super(action);
        this.id = id;
        this.name = name;
    }
}
