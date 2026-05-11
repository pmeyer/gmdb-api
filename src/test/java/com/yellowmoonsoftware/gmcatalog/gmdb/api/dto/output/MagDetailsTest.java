package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MagDetailsTest {

    @Test
    void exposesMagazineValuesAndInheritedResourceState() {
        final MagDetails details = new MagDetails("12", "5", "Special Issue");

        assertThat(details.volume()).isEqualTo("12");
        assertThat(details.issue()).isEqualTo("5");
        assertThat(details.issueName()).isEqualTo("Special Issue");
        assertThat(details.resourceId()).isNull();
        assertThat(details.resources()).isEmpty();
    }

    @Test
    void coverIsNullWhenNoCoverImageResourceExists() {
        final MagDetails details = new MagDetails("12", "5", "Special Issue");

        assertThat(details.cover()).isNull();
    }

    @Test
    void toStringIncludesMagazineValues() {
        final MagDetails details = new MagDetails("12", "5", "Special Issue");

        assertThat(details).hasToString("MagDetails(volume=12, issue=5, issueName=Special Issue)");
    }
}
