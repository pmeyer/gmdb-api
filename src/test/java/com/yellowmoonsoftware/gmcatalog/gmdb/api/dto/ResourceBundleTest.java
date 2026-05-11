package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceBundleTest {

    private static final UUID RESOURCE_ID = UUID.fromString("1d6df257-0dd0-4d02-91dd-37fd3f4e075c");

    private record TestResourceBundle(UUID resourceId, Map<ResourceSlug, ResourceAttributes> resources)
            implements ResourceBundle {
    }

    @Test
    void getResourceUrlReturnsUrlWhenAttributesExistForSlug() {
        final Map<ResourceSlug, ResourceAttributes> resources = new EnumMap<>(ResourceSlug.class);
        resources.put(ResourceSlug.COVER_IMAGE, new ResourceAttributes("cover.jpg", MediaType.IMAGE_JPEG));
        final ResourceBundle bundle = new TestResourceBundle(RESOURCE_ID, resources);

        assertThat(bundle.getResourceUrl(ResourceSlug.COVER_IMAGE))
                .contains("/resources/pub/1d6df257-0dd0-4d02-91dd-37fd3f4e075c/cover-img?_f=cover.jpg&_mt=image/jpeg");
    }

    @Test
    void getResourceUrlReturnsEmptyWhenAttributesDoNotExistForSlug() {
        final ResourceBundle bundle = new TestResourceBundle(RESOURCE_ID, new EnumMap<>(ResourceSlug.class));

        assertThat(bundle.getResourceUrl(ResourceSlug.COVER_IMAGE)).isEmpty();
    }
}
