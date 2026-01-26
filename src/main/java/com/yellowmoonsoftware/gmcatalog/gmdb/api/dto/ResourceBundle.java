package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.config.FileResourceConfiguration;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;

import java.util.Optional;
import java.util.UUID;

public interface ResourceBundle extends ResourcesContainer {
    UUID resourceId();

    default Optional<String> getResourceUrl(final ResourceSlug slug) {
        return Optional.ofNullable(resources().get(slug))
                .map(attr -> slug.getPath(this.resourceId(), attr.originalFilename(), attr.mediaType(), FileResourceConfiguration.RESOURCE_PATH));
    }
}

