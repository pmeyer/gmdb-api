package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceReferenceTest {

    @Test
    void exposesResourceReferenceValues() {
        final ResourceReference reference = new ResourceReference(ResourceSlug.COVER_IMAGE, "pub/id/cover-img", "cover.jpg");

        assertThat(reference.resourceSlug()).isEqualTo(ResourceSlug.COVER_IMAGE);
        assertThat(reference.slugPath()).isEqualTo("pub/id/cover-img");
        assertThat(reference.originalName()).isEqualTo("cover.jpg");
    }

    @Test
    void supportsRecordEqualityHashCodeAndToString() {
        final ResourceReference reference = new ResourceReference(ResourceSlug.ALBUM_ART, "album/id/album-art", "art.jpg");
        final ResourceReference same = new ResourceReference(ResourceSlug.ALBUM_ART, "album/id/album-art", "art.jpg");
        final ResourceReference different = new ResourceReference(ResourceSlug.TRANSCRIPTION, "transcription/id/transcription", "song.pdf");

        assertThat(reference)
                .isEqualTo(same)
                .hasSameHashCodeAs(same)
                .isNotEqualTo(different)
                .hasToString("ResourceReference[resourceSlug=ALBUM_ART, slugPath=album/id/album-art, originalName=art.jpg]");
    }
}
