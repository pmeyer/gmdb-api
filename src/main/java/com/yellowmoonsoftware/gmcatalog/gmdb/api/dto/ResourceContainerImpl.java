package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class ResourceContainerImpl implements ResourcesContainer {
    @JsonProperty
    private final Map<ResourceSlug, ResourceAttributes> resources = new java.util.HashMap<>();
}
