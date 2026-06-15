package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MutationSchemaTest {

    @Test
    void bookEditionInputRequiresInfoAndEditionWithoutInfoDefault() throws IOException {
        final String schema = Files.readString(Path.of("src/main/resources/graphql/mutation.graphqls"));

        assertThat(schema)
            .contains("info: BookEditionInput!\n")
            .contains("edition: String!\n")
            .doesNotContain("info: BookEditionInput! = { }");
    }
}
