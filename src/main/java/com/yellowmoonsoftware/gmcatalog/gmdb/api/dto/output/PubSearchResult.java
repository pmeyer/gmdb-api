package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@ToString
@Accessors(fluent = true)
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PubSearchResult {
    @EqualsAndHashCode.Include
    private final Long id;
    private final String name;
    private final PubType type;
    private final PubDetails details;
    private final LocalDate pubDate;
    private final String serialNumber;
    private final Long pubIndexId;
}

