package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum ResourceSlug {
    COVER_IMAGE("cover_img", "pub/{id}"),
    ALBUM_ART("album-art", "album/{id}"),
    TRANSCRIPTION("transcription", "transcription/{id}"),;

    private final String slugName;

    private final String pathPattern;

    private static final Map<String, ResourceSlug> slugMap = Arrays.stream(ResourceSlug.values())
            .collect(toMap(ResourceSlug::slugName, v -> v));

    public String getPath(final Map<String, ?> variables) {
        return getPath(variables, null);
    }

    public String getPath(final Map<String, ?> variables, final String fileName) {
        return UriComponentsBuilder.fromPath(pathPattern)
                .pathSegment(slugName())
                .queryParamIfPresent("_f", Optional.ofNullable(fileName))
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
