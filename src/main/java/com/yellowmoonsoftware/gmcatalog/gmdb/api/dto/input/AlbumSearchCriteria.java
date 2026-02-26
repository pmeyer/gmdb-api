package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderSpec;

import java.time.LocalDate;
import java.util.LinkedHashSet;

public record AlbumSearchCriteria(
    String searchName,
    LocalDate dateStart,
    LocalDate dateEnd,
    LinkedHashSet<OrderSpec<OrderBy>> orderBy
) {
    enum OrderBy {
        RELEASE_DATE,
        TITLE,
        ARTIST
    }
}
