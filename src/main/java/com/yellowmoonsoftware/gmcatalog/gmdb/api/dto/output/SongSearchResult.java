package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongSearchResult {
    @EqualsAndHashCode.Include
    private final Long id;
    private final String title;
    private final Integer trackNumber;
    private final Long albumId;
}
