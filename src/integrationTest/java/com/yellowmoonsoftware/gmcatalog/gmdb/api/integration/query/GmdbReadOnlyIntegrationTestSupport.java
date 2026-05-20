package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.GmdbDatabaseIntegrationTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

abstract class GmdbReadOnlyIntegrationTestSupport extends GmdbDatabaseIntegrationTestSupport {

    protected static final GmdbIntegrationDatabase DATABASE = createStartedDatabase();

    @BeforeAll
    static void applyTestData() {
        applyBaselineTestData(DATABASE);
    }

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerGmdbIntegrationProperties(registry, DATABASE, fileRepoRoot());
    }

    protected static Path fileRepoRoot() {
        try {
            return Path.of(Objects.requireNonNull(
                            GmdbReadOnlyIntegrationTestSupport.class.getClassLoader().getResource("file-repo"),
                            "Could not find file-repo test resource")
                    .toURI());
        } catch (final URISyntaxException exception) {
            throw new IllegalStateException("Could not resolve file-repo test resource", exception);
        }
    }
}
