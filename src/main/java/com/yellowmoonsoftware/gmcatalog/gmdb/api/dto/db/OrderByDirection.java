package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum OrderByDirection {
    ASC("ASC"),
    DESC("DESC");

    @Getter
    @Accessors(fluent = true)
    private final String clause;
}

