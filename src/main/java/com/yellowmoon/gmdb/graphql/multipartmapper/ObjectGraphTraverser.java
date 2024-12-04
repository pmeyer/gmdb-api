package com.yellowmoon.gmdb.graphql.multipartmapper;

public interface ObjectGraphTraverser {
    <T> T set(String path, T value);
    ObjectGraphTraverser dereference(String path);
}
