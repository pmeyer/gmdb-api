package com.yellowmoon.gmdb.graphql.multipartmapper;

import graphql.com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraphQlMultipartDecoderTest {
    @Spy
    GraphQlMultipartDecoder decoderSpy;

    @Captor
    ArgumentCaptor<Map<String, Part>> partsMapCaptor;

    @Captor
    ArgumentCaptor<GraphQlMultipartDecoder.PartKey> partKeyCaptor;

    Map<String, Object> expectedResult;
    Map<String, Part> partsMapParam;

    @BeforeEach
    void setup() {
        expectedResult = Maps.newHashMap();
        partsMapParam = Maps.newHashMap();
        when(decoderSpy.decodePart(any(),any()))
                .thenReturn(Mono.just(expectedResult));
    }

    @Test
    void decodeOperationsCallsDecodePartWithExpectedParams() {
        expectedResult.put("query", "query FooQuery($foo: String) { foo(foo: $foo) }");
        expectedResult.put("variables", Map.of("foo", "bar"));
        StepVerifier.create(decoderSpy.decodeOperations(partsMapParam))
                .assertNext(part -> {
                    assertThat(part.query())
                            .isEqualTo(expectedResult.get("query"));
                    assertThat(part.variables())
                            .isEqualTo(expectedResult.get("variables"));
                    verify(decoderSpy, times(1))
                            .decodePart(partsMapCaptor.capture(), partKeyCaptor.capture());
                    assertThat(partsMapCaptor.getValue())
                            .isSameAs(partsMapParam);
                    assertThat(partKeyCaptor.getValue())
                            .isEqualTo(GraphQlMultipartDecoder.PartKey.OPERATIONS);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void decodeFileMapCallsDecodePartWithExpectedParams() {
        expectedResult.put("0", List.of("variables.files.0"));
        StepVerifier.create(decoderSpy.decodeFileMap(partsMapParam))
                .assertNext(_ -> {
//                    assertThat(part.entrySet())
//                            .containsExactly(new GraphQlFileMapPart.Entry("0", List.of("variables.files.0"), ));
                    verify(decoderSpy, times(1))
                            .decodePart(partsMapCaptor.capture(), partKeyCaptor.capture());
                    assertThat(partsMapCaptor.getValue())
                            .isSameAs(partsMapParam);
                    assertThat(partKeyCaptor.getValue())
                            .isEqualTo(GraphQlMultipartDecoder.PartKey.MAP);
                })
                .expectComplete()
                .verify();
    }
}