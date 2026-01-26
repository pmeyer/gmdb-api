package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
@Accessors(fluent = true)
public class BookDetails extends PubDetails {

    @JsonProperty
    private final String edition;

    @JsonCreator
    public BookDetails(final String edition) {
        super((UUID) null);
        this.edition = edition;
    }
}
