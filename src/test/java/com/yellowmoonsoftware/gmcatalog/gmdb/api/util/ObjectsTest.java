package com.yellowmoonsoftware.gmcatalog.gmdb.api.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectsTest {

    private record Sample(String value) {
    }

    @Test
    void getIfNotNullReturnsAccessorValueWhenObjectIsPresent() {
        final String result = Objects.getIfNotNull(new Sample("value"), Sample::value);

        assertThat(result).isEqualTo("value");
    }

    @Test
    void getIfNotNullReturnsNullWhenObjectIsNull() {
        final String result = Objects.getIfNotNull(null, Sample::value);

        assertThat(result).isNull();
    }

    @Test
    void safeGetterReturnsAccessorValueWhenObjectIsPresent() {
        final Objects.Getter<Sample> getter = Objects.safeGetter(new Sample("value"));

        assertThat(getter.get(Sample::value)).isEqualTo("value");
    }

    @Test
    void safeGetterReturnsNullWhenObjectIsNull() {
        final Objects.Getter<Sample> getter = Objects.safeGetter(null);

        assertThat(getter.get(Sample::value)).isNull();
    }
}
