package com.yellowmoonsoftware.gmdb.dto.input;

import java.time.LocalDate;

public record AlbumSearchCriteria(
    String searchName,
    LocalDate dateStart,
    LocalDate dateEnd
) { }
