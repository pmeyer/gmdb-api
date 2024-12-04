package com.yellowmoon.gmdb.graphql.multipartmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.GraphQlRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraphQlMultipartRequestTest {

    @Mock
    GraphQlFileMapEntry mockFileEntry;

    @Mock
    GraphQlOperationsPart mockOperations;

    @Mock
    GraphQlFileMapPart mockFileMap;

    @Captor
    ArgumentCaptor<Collection<GraphQlFileMapEntry>> fileEntryCaptor;

    @Test
    void testCreateRequest() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> rawOperationsMap = objectMapper.readValue(GqlTestData.getTestOperationsJson(), new TypeReference<>() { });
        Map<String, List<String>> rawFileMap = objectMapper.readValue(GqlTestData.getTestFileMapJson(), new TypeReference<>() { });
        Map<String, Part> files = GqlTestData.generateFakeFileParts(2).collect(toMap(FilePart::name, f -> f));

        GraphQlOperationsPart ops = new GraphQlOperationsPart(rawOperationsMap);
        GraphQlFileMapPart map = new GraphQlFileMapPart(rawFileMap, files);

        final GraphQlMultipartRequest request = new GraphQlMultipartRequest(ops, map);

        assertThat(request.operations()).isSameAs(ops);
        assertThat(request.fileMap().fileEntrySet())
                .containsExactlyInAnyOrderElementsOf(
                        Set.of(
                                new GraphQlFileMapEntry(rawFileMap.get("0").getFirst(), (FilePart)files.get("0")),
                                new GraphQlFileMapEntry(rawFileMap.get("1").getFirst(), (FilePart)files.get("1"))
                        )
                );

    }

    @Test
    void testToMapCallToMapOnContainedOperationsPassingContainedFileMap() {
        final Set<GraphQlFileMapEntry> mockFileEntrySet = Set.of(mockFileEntry);
        final Map<String, Object> dummyResponse = Map.of("foo", "bar");
        when(mockFileMap.fileEntrySet())
                .thenReturn(mockFileEntrySet);
        when(mockOperations.toMap(fileEntryCaptor.capture()))
                .thenReturn(dummyResponse);

        final GraphQlMultipartRequest request = new GraphQlMultipartRequest(mockOperations, mockFileMap);
        final Map<String, Object> actual = request.toMap();

        assertThat(actual).isEqualTo(dummyResponse);
        assertThat(fileEntryCaptor.getValue()).isSameAs(mockFileEntrySet);
    }

    @Test
    void testToGraphQlRequestCallsToGraphQlRequestOnContainedOperationsPassingContainedFileMap() {
        final Set<GraphQlFileMapEntry> mockFileEntrySet = Set.of(mockFileEntry);
        final GraphQlRequest dummyResponse = mock(GraphQlRequest.class);

        when(mockFileMap.fileEntrySet())
                .thenReturn(mockFileEntrySet);
        when(mockOperations.toGraphQlRequest(fileEntryCaptor.capture()))
                .thenReturn(dummyResponse);

        final GraphQlMultipartRequest request = new GraphQlMultipartRequest(mockOperations, mockFileMap);
        final GraphQlRequest actual = request.toGraphQlRequest();

        assertThat(actual).isEqualTo(dummyResponse);
        assertThat(fileEntryCaptor.getValue()).isSameAs(mockFileEntrySet);
    }
}