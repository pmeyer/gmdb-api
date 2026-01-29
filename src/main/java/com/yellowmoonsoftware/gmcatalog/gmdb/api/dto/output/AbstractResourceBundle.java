package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceBundle;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class AbstractResourceBundle implements ResourceBundle {

    @JsonProperty
    private final UUID resourceId;

    @JsonProperty
    private final Map<ResourceSlug, ResourceAttributes> resources = new HashMap<>();
}
