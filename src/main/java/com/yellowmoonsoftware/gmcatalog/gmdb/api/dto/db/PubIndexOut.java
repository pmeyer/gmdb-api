package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PubIndexOut extends UpsertResult {
    private final Long id;
    private final String name;
    private final PubType type;
    private final String serialNumber;

    public PubIndexOut(final Long id, final String name, final PubType type, final String serialNumber, final MergeAction action) {
        super(action);
        this.id = id;
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
    }
}

