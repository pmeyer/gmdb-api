package com.yellowmoonsoftware.gmdb.util;

import java.util.function.BinaryOperator;

public interface ExtendedBinaryOperator<T> extends BinaryOperator<T> {
    static <T> ExtendedBinaryOperator<T> firstArg() {
        return (a, _) -> a;
    }
    
    static <T> ExtendedBinaryOperator<T> lastArg() {
        return (_, b) -> b;
    }
    
    static <T> ExtendedBinaryOperator<T> firstNonNull() {
        return (a, b) -> a != null ? a : b;
    }
    
    static <T> ExtendedBinaryOperator<T> lastNonNull() {
        return (a, b) -> b != null ? b : a;
    }

    static <T> ExtendedBinaryOperator<T> firstNonNull(final T defaultValue) {
        return (a, b) -> a != null ? a : b != null ? b : defaultValue;
    }
}
