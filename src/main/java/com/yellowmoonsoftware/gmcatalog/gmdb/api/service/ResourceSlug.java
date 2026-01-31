package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum ResourceSlug {
    COVER_IMAGE("cover-img", "pub/{id}"),
    ALBUM_ART("album-art", "album/{id}"),
    TRANSCRIPTION("transcription", "transcription/{id}"),;

    private final String slugName;

    private final String pathPattern;

    private static final Map<String, ResourceSlug> slugMap = Arrays.stream(ResourceSlug.values())
            .collect(toMap(ResourceSlug::slugName, v -> v));

    public String getPath(final Map<String, ?> variables) {
        return getPath(variables, null, null, null);
    }

    public String getPath(final UUID resourceId, final String fileName, final MediaType mediaType, final String rootPath) {
        return getPath(Collections.singletonMap("id", resourceId), fileName, mediaType, rootPath);
    }

    public String getPath(final Map<String, ?> variables, final String fileName, final MediaType mediaType, final String rootPath) {
        return Optional.ofNullable(rootPath)
                .map(root -> UriComponentsBuilder.fromPath(root).pathSegment(pathPattern))
                .orElseGet(() -> UriComponentsBuilder.fromPath(pathPattern))
                .pathSegment(slugName())
                .queryParamIfPresent("_f", Optional.ofNullable(fileName))
                .queryParamIfPresent("_mt", Optional.ofNullable(mediaType))
                .buildAndExpand(variables)
                .toString();
    }

    public String getRouterPath() {
        return "/%s/{slug:%s}".formatted(pathPattern, slugName);
    }

    public static ResourceSlug getResourceSlugByName(final String slugName) {
        return slugMap.get(slugName);
    }
}
