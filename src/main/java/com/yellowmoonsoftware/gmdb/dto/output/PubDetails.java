package com.yellowmoonsoftware.gmdb.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
@Accessors(fluent = true)
@RequiredArgsConstructor
@JsonSubTypes({
        @JsonSubTypes.Type(value = MagDetails.class, name = "MAG"),
        @JsonSubTypes.Type(value = BookDetails.class, name = "BOOK")
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type", include = JsonTypeInfo.As.PROPERTY)
public abstract class PubDetails {
    @JsonProperty
    private final String cover;
}
