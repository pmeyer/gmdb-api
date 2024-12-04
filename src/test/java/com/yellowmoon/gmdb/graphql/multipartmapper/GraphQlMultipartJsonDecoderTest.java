package com.yellowmoon.gmdb.graphql.multipartmapper;

import com.jayway.jsonpath.*;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQlMultipartJsonDecoderTest {
    Jackson2JsonDecoder decoder;

    GraphQlMultipartJsonDecoder mpGqlJsonDecoder;

    @BeforeAll
    static void fixtureSetup() {
        JsonPathTestConfig.integrateJackson();
    }

    @BeforeEach
    void setup() {
        decoder = new Jackson2JsonDecoder();
        mpGqlJsonDecoder = new GraphQlMultipartJsonDecoder(decoder);
    }

    @Test
    void testDecodesOperations() {
        final Mono<Map<String, Object>> actual = mpGqlJsonDecoder.decodePart(GqlTestData.getTestMultipartDataValueMap().toSingleValueMap(),
                GraphQlMultipartDecoder.PartKey.OPERATIONS);

        final DocumentContext opsDocCtx = JsonPath.parse(GqlTestData.getTestOperationsJson());
        final String expectedQuery = opsDocCtx.read("$.query", String.class);
        final Map<String, Object> expectedVars = opsDocCtx.read("$.variables", new TypeRef<>() { });

        StepVerifier.create(actual)
                .assertNext(ops -> {
                    assertThat(ops.keySet()).containsExactlyInAnyOrder("query", "variables");
                    assertThat(ops.get("query")).isEqualTo(expectedQuery);
                    assertThat(ops.get("variables"))
                            .asInstanceOf(InstanceOfAssertFactories.MAP)
                            .containsExactlyInAnyOrderEntriesOf(expectedVars);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testDecodesMap() {
        final Mono<Map<String, List<String>>> actual = mpGqlJsonDecoder.decodePart(GqlTestData.getTestMultipartDataValueMap().toSingleValueMap(),
                GraphQlMultipartDecoder.PartKey.MAP);

        final DocumentContext mapDocCtx = JsonPath.parse(GqlTestData.getTestFileMapJson());
        final Map<String, List<String>> expectedMap = mapDocCtx.read("$", new TypeRef<>() { });

        StepVerifier.create(actual)
                .assertNext(map -> {
                    assertThat(map.keySet()).containsExactlyInAnyOrderElementsOf(expectedMap.keySet());
                    assertThat(map).containsExactlyInAnyOrderEntriesOf(expectedMap);
                })
                .expectComplete()
                .verify();
    }
}