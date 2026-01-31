package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MagDetails.class, name = "MAG"),
        @JsonSubTypes.Type(value = BookDetails.class, name = "BOOK")
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type", include = JsonTypeInfo.As.PROPERTY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PubDetails extends AbstractResourceBundle {
    protected PubDetails() {
        super(null);
    }

    @Getter(lazy = true)
    private final String cover = getResourceUrl(ResourceSlug.COVER_IMAGE).orElse(null);
}
