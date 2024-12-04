package com.yellowmoon.gmdb.dto.input;

import com.yellowmoon.gmdb.dto.PubType;

import java.time.LocalDate;

public record PubSearchCriteria(
        String searchName,
        PubType type,
        LocalDate dateStart,
        LocalDate dateEnd,
        Boolean hasTranscriptions
) { }
