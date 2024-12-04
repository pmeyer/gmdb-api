package com.yellowmoon.gmdb.graphql.multipartmapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MapListGraphTraverser<TKey> implements ObjectGraphTraverser {
    private final Function<String, TKey> keyConvert;
    private final Function<TKey, Boolean> hasKey;
    private final Function<TKey, Object> extractor;
    private final BiFunction<TKey, Object, Object> mutator;

    @SuppressWarnings("unchecked")
    public <T> T set(final String path, final T value) {
        final TKey key = tryKeyAccess(path);
        if (key != null) {
            return (T)mutator.apply(key, value);
        }
        return null;
    }

    public ObjectGraphTraverser dereference(final String path) {
        final TKey key = tryKeyAccess(path);
        if (key != null) {
            return MapListGraphTraverser.wrap(extractor.apply(key));
        } else {
            return NullObjectGraphTraverser.instance;
        }
    }

    protected TKey tryKeyAccess(final String path) {
        try {
            TKey key = keyConvert.apply(path);
            return hasKey.apply(key) ? key : null;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ObjectGraphTraverser wrap(final Object obj) {
        return switch (obj) {
            case List l -> new MapListGraphTraverser<>(Integer::parseInt, i -> i < l.size() && i >= 0, l::get, (i, v) -> l.set(i, v));
            case Map m -> new MapListGraphTraverser<>(Function.identity(), m::containsKey, m::get, (k, v) -> m.put(k, v));
            case null, default -> NullObjectGraphTraverser.instance;
        };
    }
}