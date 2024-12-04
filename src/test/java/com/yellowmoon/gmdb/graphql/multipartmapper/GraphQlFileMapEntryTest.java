package com.yellowmoon.gmdb.graphql.multipartmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQlFileMapEntryTest {
    @Test
    void testFileMapEntryContainsExpectedSegmentsAndKey() {
        final FilePart filePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();
        final GraphQlFileMapEntry entry = new GraphQlFileMapEntry("variables.files.0", filePart);

        assertThat(entry.file()).isSameAs(filePart);
        assertThat(entry.path()).isEqualTo("variables.files.0");
        assertThat(entry.key()).isEqualTo("0");
        assertThat(entry.pathSegments())
                .containsExactly("variables", "files");
    }

    @Test
    void testFileMapEntryWithNullOrEmptyPathHasEmptyPathSegmentsAndNullKey() {
        final FilePart filePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();
        final GraphQlFileMapEntry nullPathEntry = new GraphQlFileMapEntry(null, filePart);

        assertThat(nullPathEntry.file()).isSameAs(filePart);
        assertThat(nullPathEntry.path()).isEqualTo(null);
        assertThat(nullPathEntry.key()).isEqualTo(null);
        assertThat(nullPathEntry.pathSegments()).isEmpty();
        assertThat(nullPathEntry.isValid()).isFalse();

        final GraphQlFileMapEntry blankPathEntry = new GraphQlFileMapEntry("", filePart);

        assertThat(blankPathEntry.file()).isSameAs(filePart);
        assertThat(blankPathEntry.path()).isEqualTo("");
        assertThat(blankPathEntry.key()).isEqualTo(null);
        assertThat(blankPathEntry.pathSegments()).isEmpty();
        assertThat(blankPathEntry.isValid()).isFalse();
    }

    @Test
    void testFileMapEntryValidWithProperPath() {
        final FilePart filePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();

        final GraphQlFileMapEntry validEntry = new GraphQlFileMapEntry("variables.files.0", filePart);
        assertThat(validEntry.isValid()).isTrue();
    }

    @Test
    void testFileMapEntryNotValidWithPathThatDoesNotStartWithVariables() {
        final FilePart filePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();

        final GraphQlFileMapEntry invalidEntry = new GraphQlFileMapEntry("files.0", filePart);
        assertThat(invalidEntry.isValid()).isFalse();
    }

    @Test
    void testFileMapEntryNotValidWithPathThatDoesNotContainAtLeastTwoSegments() {
        final FilePart filePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();

        final GraphQlFileMapEntry invalidEntry = new GraphQlFileMapEntry("variables", filePart);
        assertThat(invalidEntry.isValid()).isFalse();

        final GraphQlFileMapEntry validEntry = new GraphQlFileMapEntry("variables.0", filePart);
        assertThat(validEntry.isValid()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testReplaceGraphQlVariablesReplacesVariablesInOperationsWithFileParts() throws JsonProcessingException {
        final FilePart filePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();
        final GraphQlFileMapEntry entry = new GraphQlFileMapEntry("variables.files.0", filePart);

        final ObjectMapper mapper = new ObjectMapper();
        final GraphQlOperationsPart ops = new GraphQlOperationsPart(mapper.readValue(GqlTestData.getTestOperationsJson(), new TypeReference<>() { }));

        final List<FilePart> files = (List<FilePart>)ops.variables().get("files");
        assertThat(files).containsOnlyNulls();

        entry.replaceGraphQlVariable(ops);

        assertThat(files.getFirst()).isSameAs(filePart);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testReplaceGraphQlVariablesDoesNotReplaceVariableInOperationsWhenFileEntryIsInvalid() throws JsonProcessingException {
        final FilePart filePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();
        final GraphQlFileMapEntry entry = new GraphQlFileMapEntry("files.0", filePart);

        final ObjectMapper mapper = new ObjectMapper();
        final GraphQlOperationsPart ops = new GraphQlOperationsPart(mapper.readValue(GqlTestData.getTestOperationsJson(), new TypeReference<>() { }));

        final List<FilePart> files = (List<FilePart>)ops.variables().get("files");
        assertThat(files).containsOnlyNulls();

        entry.replaceGraphQlVariable(ops);

        assertThat(files).containsOnlyNulls();
    }
}