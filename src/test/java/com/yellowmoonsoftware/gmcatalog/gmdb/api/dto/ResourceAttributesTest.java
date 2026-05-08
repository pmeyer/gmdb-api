package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceAttributesTest {

    @Test
    void exposesResourceAttributeValues() {
        final ResourceAttributes attributes = new ResourceAttributes("cover.jpg", MediaType.IMAGE_JPEG);

        assertThat(attributes.originalFilename()).isEqualTo("cover.jpg");
        assertThat(attributes.mediaType()).isEqualTo(MediaType.IMAGE_JPEG);
    }

    @Test
    void supportsRecordEqualityHashCodeAndToString() {
        final ResourceAttributes attributes = new ResourceAttributes("cover.jpg", MediaType.IMAGE_JPEG);
        final ResourceAttributes same = new ResourceAttributes("cover.jpg", MediaType.IMAGE_JPEG);
        final ResourceAttributes different = new ResourceAttributes("cover.png", MediaType.IMAGE_PNG);

        assertThat(attributes)
                .isEqualTo(same)
                .hasSameHashCodeAs(same)
                .isNotEqualTo(different)
                .hasToString("ResourceAttributes[originalFilename=cover.jpg, mediaType=image/jpeg]");
    }
}
