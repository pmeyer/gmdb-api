package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceBundle;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public abstract class PubDetails implements ResourceBundle {
    protected PubDetails() {
        this((UUID) null);
    }

    @Getter(lazy = true)
    private final String cover = getResourceUrl(ResourceSlug.COVER_IMAGE).orElse(null);
    @JsonProperty
    private final UUID resourceId;
    @JsonProperty
    private final Map<ResourceSlug, ResourceAttributes> resources = new HashMap<>();
}
