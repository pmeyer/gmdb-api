package com.yellowmoon.gmdb.graphql.multipartmapper;

public class NullObjectGraphTraverser implements ObjectGraphTraverser {

    public static final NullObjectGraphTraverser instance = new NullObjectGraphTraverser();

    protected NullObjectGraphTraverser() { }

    @Override
    public <T> T set(String path, T value) {
        return null;
    }

    @Override
    public ObjectGraphTraverser dereference(String path) {
        return NullObjectGraphTraverser.instance;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullObjectGraphTraverser;
    }

    @Override
    public int hashCode() {
        return NullObjectGraphTraverser.class.hashCode();
    }
}
