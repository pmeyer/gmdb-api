package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongDetailsTest {

    @Test
    void exposesTrackNumberAndEmptyResourceState() {
        final SongDetails details = new SongDetails(3);

        assertThat(details.trackNumber()).isEqualTo(3);
        assertThat(details.resourceId()).isNull();
        assertThat(details.resources()).isEmpty();
    }
}
