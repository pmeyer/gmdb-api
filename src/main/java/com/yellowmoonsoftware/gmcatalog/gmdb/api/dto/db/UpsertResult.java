package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public abstract class UpsertResult {
    private final MergeAction action;
}
