package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.MagDetails;
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
class MagazineIssueInputTest {

    @Mock
    private FilePart cover;

    @Test
    void exposesIssueFieldsAndCover() {
        MagazineIssueInput input = new MagazineIssueInput("12", "4", "Winter", cover);

        assertThat(input.volume()).isEqualTo("12");
        assertThat(input.issue()).isEqualTo("4");
        assertThat(input.issueName()).isEqualTo("Winter");
        assertThat(input.cover()).isSameAs(cover);
    }

    @Test
    void convertsToDetailsWithCoverResource() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(cover.filename()).thenReturn("cover.jpg");
        when(cover.headers()).thenReturn(headers);
        MagazineIssueInput input = new MagazineIssueInput("12", "4", "Winter", cover);

        MagDetails details = input.toDetails();

        assertThat(details.volume()).isEqualTo("12");
        assertThat(details.issue()).isEqualTo("4");
        assertThat(details.issueName()).isEqualTo("Winter");
        assertThat(details.resources())
            .containsEntry(ResourceSlug.COVER_IMAGE, new ResourceAttributes("cover.jpg", MediaType.IMAGE_JPEG));
    }

    @Test
    void convertsToDetailsWithoutCoverResourceWhenCoverIsNull() {
        MagazineIssueInput input = new MagazineIssueInput("12", "4", "Winter", null);

        MagDetails details = input.toDetails();

        assertThat(details.volume()).isEqualTo("12");
        assertThat(details.issue()).isEqualTo("4");
        assertThat(details.issueName()).isEqualTo("Winter");
        assertThat(details.resources()).isEmpty();
    }
}
