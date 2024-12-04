package com.yellowmoon.gmdb.graphql.multipartmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.GraphQlRequest;
import org.springframework.http.codec.multipart.FilePart;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraphQlOperationsPartTest {

    GraphQlOperationsPart fullOpsPart;

    DocumentContext fullOpsDoc = JsonPath.parse(GqlTestData.getTestOperationsJson(true, true));

    GraphQlOperationsPart emptyOpsPart;

    GraphQlOperationsPart nullOpsPart;

    @Captor
    ArgumentCaptor<Collection<GraphQlFileMapEntry>> fileEntryCaptor;

    @BeforeEach
    void setup() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        fullOpsPart = new GraphQlOperationsPart(mapper.readValue(GqlTestData.getTestOperationsJson(true, true),
                new TypeReference<>() { }));
        emptyOpsPart = new GraphQlOperationsPart(Collections.emptyMap());
        final HashMap<String, Object> nullOpsVals = new HashMap<>();
        nullOpsVals.put("query", null);
        nullOpsVals.put("operationName", null);
        nullOpsVals.put("variables", null);
        nullOpsVals.put("extensions", null);
        nullOpsPart = new GraphQlOperationsPart(nullOpsVals);
    }

    @Test
    void testQueryReturnsExpectedValue() {
        final String expectedQuery = fullOpsDoc.read("$.query", String.class);
        assertThat(fullOpsPart.query()).isEqualTo(expectedQuery);
    }

    @Test
    void testOperationNameReturnsExpectedValue() {
        final String expectedOperationName = fullOpsDoc.read("$.operationName", String.class);
        assertThat(fullOpsPart.operationName()).isEqualTo(expectedOperationName);
    }

    @Test
    void testVariablesReturnsExpectedValue() {
        final Map<String, Object> expectedVariables = fullOpsDoc.read("$.variables", new TypeRef<>() { });
        assertThat(fullOpsPart.variables()).containsExactlyInAnyOrderEntriesOf(expectedVariables);
    }

    @Test
    void testExtensionsReturnsExpectedValue() {
        final Map<String, Object> expectedExtensions = fullOpsDoc.read("$.extensions", new TypeRef<>() { });
        assertThat(fullOpsPart.extensions()).containsExactlyInAnyOrderEntriesOf(expectedExtensions);
    }

    @Test
    void testQueryReturnsNullWhenNotPresent() {
        assertThat(emptyOpsPart.query()).isNull();
    }

    @Test
    void testOperationNameReturnsNullWhenNotPresent() {
        assertThat(emptyOpsPart.operationName()).isNull();
    }

    @Test
    void testVariablesReturnsEmptyMapWhenNotPresent() {
        assertThat(emptyOpsPart.variables()).isEmpty();
    }

    @Test
    void testExtensionsReturnsEmptyMapWhenNotPresent() {
        assertThat(emptyOpsPart.extensions()).isEmpty();
    }

    @Test
    void testQueryReturnsNullWhenValueForKeyIsNull() {
        assertThat(nullOpsPart.query()).isNull();
    }

    @Test
    void testOperationNameReturnsNullWhenValueForKeyIsNull() {
        assertThat(nullOpsPart.operationName()).isNull();
    }

    @Test
    void testVariablesReturnsNullWhenValueForKeyIsNull() {
        assertThat(nullOpsPart.variables()).isNull();
    }

    @Test
    void testExtensionsReturnsNullWhenValueForKeyIsNull() {
        assertThat(nullOpsPart.extensions()).isNull();
    }

    @Test
    void testDefaultValueReturnedIfMismatchedUnderlyingValue() {
        final GraphQlOperationsPart badOps = new GraphQlOperationsPart(Map.of("query", Collections.emptyList()));
        assertThat(badOps.query()).isNull();
    }

    @Test
    void testToMapReturnsMapOfOperationsParts() {
        assertThat(fullOpsPart.toMap())
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        GraphQlOperationsPart.QUERY, fullOpsPart.query(),
                        GraphQlOperationsPart.OPERATION_NAME, fullOpsPart.operationName(),
                        GraphQlOperationsPart.VARIABLES, fullOpsPart.variables(),
                        GraphQlOperationsPart.EXTENSIONS, fullOpsPart.extensions()
                ));
    }

    @Test
    void testToGraphQlRequestReturnsGraphQlRequestOfOperationsParts() {
        assertThat(fullOpsPart.toGraphQlRequest())
                .extracting(GraphQlRequest::getDocument, GraphQlRequest::getOperationName, GraphQlRequest::getVariables, GraphQlRequest::getExtensions)
                .containsExactly(fullOpsPart.query(), fullOpsPart.operationName(), fullOpsPart.variables(), fullOpsPart.extensions());
    }

    @Test
    void testToMapCallsReplaceGraphQlFileVariablesAndReturnsMapOfOperationsPartsWhenSuppliedFileEntries() {
        final GraphQlOperationsPart spy = spy(fullOpsPart);
        final List<GraphQlFileMapEntry> fileEntries = Collections.emptyList();

        doCallRealMethod()
                .when(spy)
                .replaceGraphQlFileVariables(fileEntryCaptor.capture());

        assertThat(spy.toMap(fileEntries))
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        GraphQlOperationsPart.QUERY, fullOpsPart.query(),
                        GraphQlOperationsPart.OPERATION_NAME, fullOpsPart.operationName(),
                        GraphQlOperationsPart.VARIABLES, fullOpsPart.variables(),
                        GraphQlOperationsPart.EXTENSIONS, fullOpsPart.extensions()
                ));

        assertThat(fileEntryCaptor.getValue()).isEqualTo(fileEntries);
    }

    @Test
    void testGraphQlRequestCallsReplaceGraphQlFileVariablesAndReturnsGraphQlRequestOfOperationsPartsWhenSuppliedFileEntries() {
        final GraphQlOperationsPart spy = spy(fullOpsPart);
        final List<GraphQlFileMapEntry> fileEntries = Collections.emptyList();

        doCallRealMethod()
                .when(spy)
                .replaceGraphQlFileVariables(fileEntryCaptor.capture());

        assertThat(spy.toGraphQlRequest(fileEntries))
                .extracting(GraphQlRequest::getDocument, GraphQlRequest::getOperationName, GraphQlRequest::getVariables, GraphQlRequest::getExtensions)
                .containsExactly(fullOpsPart.query(), fullOpsPart.operationName(), fullOpsPart.variables(), fullOpsPart.extensions());

        assertThat(fileEntryCaptor.getValue()).isEqualTo(fileEntries);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testReplaceGraphQlFileVariablesReplacesVariablesInOperationsWithFileParts() {
        final FilePart testFilePart = GqlTestData.generateFakeFileParts(1).toList().getFirst();
        final List<GraphQlFileMapEntry> fileEntries = Collections.singletonList(new GraphQlFileMapEntry("variables.files.0", testFilePart));

        assertThat((List<FilePart>)fullOpsPart.variables().get("files")).containsOnlyNulls();
        fullOpsPart.replaceGraphQlFileVariables(fileEntries);

        assertThat(((List<FilePart>)fullOpsPart.variables().get("files")).getFirst())
                .isEqualTo(testFilePart);
    }

    @BeforeAll
    static void fixtureSetup() {
        JsonPathTestConfig.integrateJackson();
    }
}