package com.yellowmoon.gmdb.dto.input;

import java.time.LocalDate;

public record AlbumSearchCriteria(
    String searchName,
    LocalDate dateStart,
    LocalDate dateEnd
) { }
