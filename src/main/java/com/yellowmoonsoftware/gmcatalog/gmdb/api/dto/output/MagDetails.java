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
public class MagDetails extends PubDetails {

    @JsonProperty
    private final String volume;
    @JsonProperty
    private final String issue;
    @JsonProperty
    private final String issueName;

    @JsonCreator
    public MagDetails(final String volume, final String issue, final String issueName) {
        super((UUID) null);
        this.volume = volume;
        this.issue = issue;
        this.issueName = issueName;
    }
}


