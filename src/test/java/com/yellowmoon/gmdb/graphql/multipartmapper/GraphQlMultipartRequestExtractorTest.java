package com.yellowmoon.gmdb.graphql.multipartmapper;

import com.jayway.jsonpath.*;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphQlMultipartRequestExtractorTest {

    @Mock
    GraphQlMultipartDecoder graphQlMultipartDecoder;

    @InjectMocks
    GraphQlMultipartRequestExtractor extractor;

    MultiValueMap<String, Part> expectedMultipartValues;

    @Captor
    ArgumentCaptor<Map<String, Part>> expectedPart;

    DocumentContext opsDoc = JsonPath.parse(GqlTestData.getTestOperationsJson());

    DocumentContext fileMapDoc = JsonPath.parse(GqlTestData.getTestFileMapJson());

    @BeforeAll
    static void fixtureSetup() {
        JsonPathTestConfig.integrateJackson();
    }

    @BeforeEach
    void setup() {
        expectedMultipartValues = GqlTestData.getTestMultipartDataValueMap();
        when(graphQlMultipartDecoder.decodeOperations(any()))
                .thenReturn(Mono.just(new GraphQlOperationsPart(opsDoc.read("$", new TypeRef<>() { }))));
        when(graphQlMultipartDecoder.decodeFileMap(any()))
                .thenReturn(Mono.just(new GraphQlFileMapPart(fileMapDoc.read("$", new TypeRef<>() { }),
                        expectedMultipartValues.toSingleValueMap())));
    }

    @Test
    void extractDecodesPartsAndConstructExpectedReturnObject() {
        final Mono<GraphQlMultipartRequest> actual = extractor.extract(expectedMultipartValues);

        StepVerifier.create(actual)
                .assertNext(gqlMpReq -> {
                    verify(graphQlMultipartDecoder).decodeOperations(expectedPart.capture());
                    verify(graphQlMultipartDecoder).decodeFileMap(expectedPart.capture());

                    final List<Map<String, Part>> params = expectedPart.getAllValues();
                    assertThat(params.getFirst())
                            .containsExactlyInAnyOrderEntriesOf(expectedMultipartValues.toSingleValueMap());
                    assertThat(params.getLast())
                            .containsExactlyInAnyOrderEntriesOf(expectedMultipartValues.toSingleValueMap());

                    assertThat(gqlMpReq.operations().query())
                            .isEqualTo(opsDoc.read("$.query", String.class));
                    assertThat(gqlMpReq.operations().variables())
                            .asInstanceOf(InstanceOfAssertFactories.MAP)
                            .containsExactlyInAnyOrderEntriesOf(opsDoc.read("$.variables", new TypeRef<>() { }));

                    assertThat(gqlMpReq.fileMap().fileEntrySet())
                            .filteredOn(e -> e.path().equals("variables.files.0"))
                            .first()
                            .hasFieldOrPropertyWithValue("path", "variables.files.0")
                            .hasFieldOrPropertyWithValue("key", "0")
                            .hasFieldOrPropertyWithValue("pathSegments", Arrays.asList("variables", "files"))
                            .hasFieldOrPropertyWithValue("file", expectedMultipartValues.getFirst("0"))
                            .extracting(GraphQlFileMapEntry::isValid)
                            .isEqualTo(true);

                    assertThat(gqlMpReq.fileMap().fileEntrySet())
                            .filteredOn(e -> e.path().equals("variables.files.1"))
                            .first()
                            .hasFieldOrPropertyWithValue("path", "variables.files.1")
                            .hasFieldOrPropertyWithValue("key", "1")
                            .hasFieldOrPropertyWithValue("pathSegments", Arrays.asList("variables", "files"))
                            .hasFieldOrPropertyWithValue("file", expectedMultipartValues.getFirst("1"))
                            .extracting(GraphQlFileMapEntry::isValid)
                            .isEqualTo(true);

                    assertThat(gqlMpReq.fileMap().fileEntrySet()).size().isEqualTo(2);
                })
                .expectComplete()
                .verify();
    }
}

