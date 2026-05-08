package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourcesContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PubCoverImageInputTest {

    @Mock
    private FilePart cover;

    @Test
    void exposesRecordValues() {
        PubCoverImageInput input = new PubCoverImageInput(1L, cover);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.cover()).isSameAs(cover);
    }

    @Test
    void convertsCoverToResourceDetails() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(cover.filename()).thenReturn("cover.jpg");
        when(cover.headers()).thenReturn(headers);
        PubCoverImageInput input = new PubCoverImageInput(1L, cover);

        ResourcesContainer details = input.toDetails();

        assertThat(details.resources())
            .containsEntry(ResourceSlug.COVER_IMAGE, new ResourceAttributes("cover.jpg", MediaType.IMAGE_JPEG));
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        PubCoverImageInput input = new PubCoverImageInput(1L, cover);
        PubCoverImageInput sameValues = new PubCoverImageInput(1L, cover);
        PubCoverImageInput differentId = new PubCoverImageInput(2L, cover);

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentId);
        assertThat(input.toString()).contains("id=1");
    }
}
