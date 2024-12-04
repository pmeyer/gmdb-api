package com.yellowmoon.gmdb.graphql.multipartmapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.GraphQlRequest;
import org.springframework.graphql.support.DefaultGraphQlRequest;
import org.springframework.lang.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class GraphQlOperationsPart {
    public final static String QUERY = "query";
    public final static String OPERATION_NAME = "operationName";
    public final static String VARIABLES = "variables";
    public final static String EXTENSIONS = "extensions";

    private final static ParameterizedTypeReference<Map<String, Object>> MAP_TYPE_REF = new ParameterizedTypeReference<>() {};
    private final static ParameterizedTypeReference<String> STRING_TYPE_REF = new ParameterizedTypeReference<>() {};

    private final Map<String, Object> operations;

    @Getter(lazy = true)
    private final String query = getOrDefault(operations, QUERY, () -> null, STRING_TYPE_REF);

    @Getter(lazy = true)
    private final String operationName = getOrDefault(operations, OPERATION_NAME, () -> null, STRING_TYPE_REF);

    @Getter(lazy = true)
    private final Map<String, Object> variables = getOrDefault(operations, VARIABLES, HashMap::new, MAP_TYPE_REF);

    @Getter(lazy = true)
    private final Map<String, Object> extensions = getOrDefault(operations, EXTENSIONS, HashMap::new, MAP_TYPE_REF);

    private static <T> T getOrDefault(@NonNull final Map<String, ?> map, @NonNull String key, @NonNull final Supplier<T> defaultValueSupplier, ParameterizedTypeReference<T> typeRef) {
        try {
            final Class<T> clazz = getClassForType(typeRef);

            final Object val = map.get(key);
            if (clazz.isInstance(val) || (val == null && map.containsKey(key))) {
                return clazz.cast(val);
            }

            return defaultValueSupplier.get();
        } catch (Exception e) {
            return defaultValueSupplier.get();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClassForType(final ParameterizedTypeReference<T> typeRef) throws ClassNotFoundException {
        final Type type = typeRef.getType();
        if (type instanceof Class<?> clazz) {
            return (Class<T>)clazz;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;

        return (Class<T>) Class.forName(parameterizedType.getRawType().getTypeName());
    }

    public Map<String, Object> toMap() {
        return this.toMap(null);
    }

    public Map<String, Object> toMap(final Collection<GraphQlFileMapEntry> files) {
        // Replace file variables
        Optional.ofNullable(files).ifPresent(this::replaceGraphQlFileVariables);

        return Map.of(
                QUERY, this.query(),
                OPERATION_NAME, Optional.ofNullable(this.operationName()).orElse(""),
                VARIABLES, this.variables(),
                EXTENSIONS, this.extensions()
        );
    }

    public GraphQlRequest toGraphQlRequest() {
        return this.toGraphQlRequest(null);
    }

    public GraphQlRequest toGraphQlRequest(final Collection<GraphQlFileMapEntry> files) {
        // Replace file variables
        Optional.ofNullable(files).ifPresent(this::replaceGraphQlFileVariables);

        return new DefaultGraphQlRequest(this.query(), this.operationName(), this.variables(), this.extensions());
    }

    protected void replaceGraphQlFileVariables(@NonNull final Collection<GraphQlFileMapEntry> files) {
        files.stream()
                .filter(GraphQlFileMapEntry::isValid)
                .forEach(f -> f.replaceGraphQlVariable(this));
    }
}
