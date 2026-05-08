package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class GmdbDataServiceTest {

    @Test
    void addMagazineCurrentlyPerformsNoOperation() {
        final GmdbDataService service = new GmdbDataService();

        assertThatCode(() -> service.addMagazine(null)).doesNotThrowAnyException();
    }
}
