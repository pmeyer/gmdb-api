package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class GmdbApplicationIntegrationTests extends GmdbDatabaseIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedDatabase();

    @Autowired
    private ConnectionFactory connectionFactory;

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerGmdbIntegrationProperties(registry, DATABASE);
    }

    @Test
    void startsApplicationAgainstMigratedDatabase() {
        final Mono<Integer> selectOne = Mono.usingWhen(
                Mono.from(connectionFactory.create()),
                connection -> Mono.from(connection.createStatement("select 1").execute())
                        .flatMap(result -> Mono.from(result.map((row, metadata) -> row.get(0, Integer.class)))),
                connection -> Mono.from(connection.close()));

        StepVerifier.create(selectOne)
                .expectNext(1)
                .verifyComplete();
    }
}
