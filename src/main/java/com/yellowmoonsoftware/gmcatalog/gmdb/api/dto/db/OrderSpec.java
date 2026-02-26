package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.springframework.lang.NonNull;

import java.util.Objects;

public record OrderSpec<T extends Enum<T>> (@NonNull T column, OrderByDirection direction, OrderByNulls nullsOrder) {
    @Override
    public int hashCode() {
        return Objects.hash(column);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof OrderSpec<?> other) || other.column.getClass() != column.getClass()) {
            return false;
        }

        return Objects.equals(column, other.column);
    }
}
