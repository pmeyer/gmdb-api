package com.yellowmoon.gmdb.graphql.multipartmapper;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
public class GraphQlFileMapPart {
    private final Set<GraphQlFileMapEntry> fileEntrySet;

    public GraphQlFileMapPart(final Map<String, List<String>> fileMap, final Map<String, Part> partsMap) {
        this.fileEntrySet = fileMap.entrySet().stream()
                .filter(e -> {
                    Part p;
                    return (p = partsMap.get(e.getKey())) != null && p instanceof FilePart;
                })
                .flatMap(e -> e.getValue().stream()
                        .map(p -> new GraphQlFileMapEntry(p, (FilePart) partsMap.get(e.getKey()))))
                .collect(Collectors.toSet());
    }
}
