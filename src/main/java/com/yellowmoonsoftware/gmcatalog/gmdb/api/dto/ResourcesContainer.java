package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type.JsonTypeHandler;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;

import java.util.Map;

@JsonTypeHandler(mapDescendants = true)
public interface ResourcesContainer {
    Map<ResourceSlug, ResourceAttributes> resources();

    default void addResource(@NonNull final ResourceSlug slug, @NonNull final FilePart filePart) {
        resources().put(slug, new ResourceAttributes(filePart.filename(), filePart.headers().getContentType()));
    }
}

