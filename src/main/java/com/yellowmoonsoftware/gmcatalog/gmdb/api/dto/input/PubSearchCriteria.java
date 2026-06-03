package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;

import java.time.LocalDate;

public record PubSearchCriteria(
        Long id,
        String searchName,
        PubType type,
        LocalDate dateStart,
        LocalDate dateEnd,
        Boolean hasTranscriptions
) { }
