package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class GmdbApplicationIntegrationTests extends GmdbDatabaseIntegrationTestSupport {

    @Autowired
    private ConnectionFactory connectionFactory;

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
