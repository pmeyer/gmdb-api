package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum OrderByNulls {
    NULLS_FIRST("NULLS FIRST"),
    NULLS_LAST("NULLS LAST");

    @Getter
    @Accessors(fluent = true)
    private final String clause;
}
