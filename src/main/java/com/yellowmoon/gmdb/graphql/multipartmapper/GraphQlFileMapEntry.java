package com.yellowmoon.gmdb.graphql.multipartmapper;

import com.yellowmoon.gmdb.util.ExtendedBinaryOperator;
import graphql.com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class GraphQlFileMapEntry {
    private static final Pattern PERIOD = Pattern.compile("\\.");

    @EqualsAndHashCode.Include
    private final String path;
    private final FilePart file;
    private final List<String> pathSegments;
    private final String key;

    public GraphQlFileMapEntry(final String path, final FilePart file) {
        this.path = path;
        this.file = file;

        final List<String> segments = Optional.ofNullable(path)
                .filter(p -> !p.isEmpty())
                .map(p -> Lists.newArrayList(PERIOD.split(p)))
                .orElse(Lists.newArrayList());
        this.key = !segments.isEmpty() ? segments.removeLast() : null;
        this.pathSegments = Collections.unmodifiableList(segments);
    }

    public boolean isValid() {
        return this.key != null && !this.pathSegments().isEmpty()
                && this.pathSegments().getFirst().equals("variables");
    }

    public void replaceGraphQlVariable(final GraphQlOperationsPart opsPart) {
        if (!isValid()) {
            return;
        }

        this.pathSegments()
                .stream()
                .skip(1) // skip "variables"
                .reduce(MapListGraphTraverser.wrap(opsPart.variables()),
                        ObjectGraphTraverser::dereference,
                        ExtendedBinaryOperator.firstArg())
                .set(this.key(), this.file());
    }
}


