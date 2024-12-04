package com.yellowmoon.gmdb.graphql.multipartmapper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NullObjectGraphTraverserTest {
    NullObjectGraphTraverser nullMapper = NullObjectGraphTraverser.instance;

    @Test
    void testDereferenceWithNullPath() {
        ObjectGraphTraverser result = nullMapper.dereference(null);
        assertThat(result).isSameAs(NullObjectGraphTraverser.instance);
    }

    @Test
    void testDereferenceWithEmptyPath() {
        ObjectGraphTraverser result = nullMapper.dereference("");
        assertThat(result).isSameAs(NullObjectGraphTraverser.instance);
    }

    @Test
    void testDereferenceReturnsSameInstanceAcrossCalls() {
        ObjectGraphTraverser result1 = nullMapper.dereference("one");
        ObjectGraphTraverser result2 = nullMapper.dereference("two");

        assertThat(result1)
                .isSameAs(result2)
                .isSameAs(NullObjectGraphTraverser.instance);
    }

    @Test
    void testSetReturnsNull() {
        assertThat(nullMapper.set("foo", "bar")).isNull();
        assertThat(nullMapper.set("foo", "bash")).isNull();
    }

    @Test
    void testEqualsMethod() {
        assertThat(nullMapper.equals(NullObjectGraphTraverser.instance)).isTrue();
        assertThat(nullMapper.equals(new Object())).isFalse();
    }

    @Test
    void testHashCode() {
        int hashCode1 = nullMapper.hashCode();
        int hashCode2 = NullObjectGraphTraverser.instance.hashCode();

        assertThat(hashCode1).isEqualTo(hashCode2);
    }
}