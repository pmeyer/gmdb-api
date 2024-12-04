package com.yellowmoon.gmdb.graphql.multipartmapper;

import graphql.com.google.common.collect.Maps;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GqlTestData {

    public static String getTestOperationsJson() {
        return getTestOperationsJson(false, false);
    }

    public static String getTestOperationsJson(final boolean includeOperationName, final boolean includeExtensions) {
        final String baseJson = """
                {
                    "query": "mutation($files: [Upload!]!) { multipleUpload(files: $files) { id } }",
                    "variables": {
                        "files": [null, null],
                        "foo": "bar"
                    }
                """
                + (includeOperationName ? """
                ,"operationName": "someMutation"
                """ : "")
                + (includeExtensions ? """
                ,"extensions": { "foo": "bar" }
                """ : "")
                +
                """
                }
                """;



        return includeOperationName ? baseJson.replace("mutation", "someMutation") : baseJson;
    }

    public static String getTestFileMapJson() {
        return getTestFileMapJson(false, false);
    }
    public static String getTestFileMapJson(final boolean withInvalidVariablePath, final boolean withInvalidFilePartRef) {
        final String filePath_0 = withInvalidVariablePath ? "\"variables.badVar\"" : "\"variables.files.0\"";

        final String filePartRef_1 = withInvalidFilePartRef ? "\"7x\"" : "\"1\"";

        return """
                { "0": [""" + filePath_0 + """
                ],""" + filePartRef_1 + """
              : ["variables.files.1"] }
              """;
    }

    public static FilePart getFakeTestFilePart(final String key, final String fileName, final String content) {
        return new MockFilePart(fileName, key, content);
    }

    public static MultiValueMap<String, Part> getTestMultipartDataValueMap() {
        return getTestMultipartDataValueMap(false, false, false);
    }

    public static MultiValueMap<String, Part> getTestMultipartDataValueMap(final boolean withUnmappedFile, final boolean withInvalidVariablePath, final boolean withInvalidFilePartRef) {


        HashMap<String, List<Part>> partsMap = Maps.newHashMap(Map.of(
                "operations", Collections.singletonList(new MockFormFieldPart("operations", getTestOperationsJson())),
                "map", Collections.singletonList(new MockFormFieldPart("map", getTestFileMapJson(withInvalidVariablePath, withInvalidFilePartRef)))
        ));
        generateFakeFileParts(2)
                .forEach(fp -> partsMap.put(fp.name(), Collections.singletonList(fp)));


        if (withUnmappedFile) {
            partsMap.put("2", Collections.singletonList(getFakeTestFilePart("2", "foo-file-2.txt", "Foo File Two content")));
        }

        return new MultiValueMapAdapter<>(partsMap);
    }

    public static Stream<FilePart> generateFakeFileParts(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> getFakeTestFilePart(Integer.toString(i), "foo-file-" + i + ".txt", "Foo File " + i + " content"));
    }
}
