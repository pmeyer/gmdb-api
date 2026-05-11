package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourcesContainerTest {

    @Mock
    private FilePart filePart;

    private record TestResourcesContainer(Map<ResourceSlug, ResourceAttributes> resources)
            implements ResourcesContainer {
    }

    @Test
    void addResourceStoresOriginalFilenameAndMediaTypeForSlug() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        when(filePart.filename()).thenReturn("song.pdf");
        when(filePart.headers()).thenReturn(headers);
        final Map<ResourceSlug, ResourceAttributes> resources = new EnumMap<>(ResourceSlug.class);
        final ResourcesContainer container = new TestResourcesContainer(resources);

        container.addResource(ResourceSlug.TRANSCRIPTION, filePart);

        assertThat(resources)
                .containsEntry(ResourceSlug.TRANSCRIPTION,
                        new ResourceAttributes("song.pdf", MediaType.APPLICATION_PDF));
    }
}
