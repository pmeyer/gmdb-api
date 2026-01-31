package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
public class BookDetails extends PubDetails {

    @JsonProperty
    private final String edition;

    @JsonCreator
    public BookDetails(final String edition) {
        this.edition = edition;
    }
}
