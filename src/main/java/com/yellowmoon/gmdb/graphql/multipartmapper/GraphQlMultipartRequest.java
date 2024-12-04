package com.yellowmoon.gmdb.graphql.multipartmapper;

import org.springframework.graphql.GraphQlRequest;

import java.util.*;

public record GraphQlMultipartRequest(GraphQlOperationsPart operations, GraphQlFileMapPart fileMap) {
    public Map<String, Object> toMap() {
        return this.operations().toMap(this.fileMap().fileEntrySet());
    }

    public GraphQlRequest toGraphQlRequest() {
        return this.operations().toGraphQlRequest(this.fileMap().fileEntrySet());
    }
}
