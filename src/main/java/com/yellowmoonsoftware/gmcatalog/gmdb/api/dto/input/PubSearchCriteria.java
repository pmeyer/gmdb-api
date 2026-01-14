package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;

import java.time.LocalDate;

public record PubSearchCriteria(
        String searchName,
        PubType type,
        LocalDate dateStart,
        LocalDate dateEnd,
        Boolean hasTranscriptions
) { }
