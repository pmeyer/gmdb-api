package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
@Accessors(fluent = true)
public class BookDetails extends PubDetails {

    @JsonProperty
    private final String edition;

    @JsonCreator
    public BookDetails(String cover, String edition) {
        super(cover);
        this.edition = edition;
    }
}
