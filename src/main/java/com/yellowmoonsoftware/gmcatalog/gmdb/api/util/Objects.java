package com.yellowmoonsoftware.gmcatalog.gmdb.api.util;

import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Objects {
    public static <T, R> R getIfNotNull(final T obj, final Function<T,R> accessor) {
        return Optional.ofNullable(obj)
                .map(accessor)
                .orElse(null);
    }

    public static <T> Getter<T> safeGetter(T obj) {
        final Optional<T> optOfObj = Optional.ofNullable(obj);
        return new Getter<T>() {
            @Override
            public <R> R get(Function<T, R> accessor) {
                return optOfObj.map(accessor).orElse(null);
            }
        };
    }

    public interface Getter<T> {
        <R> R get(final Function<T,R> accessor);
    }
}
