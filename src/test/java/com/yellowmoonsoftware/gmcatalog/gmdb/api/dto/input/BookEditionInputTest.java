package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
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
class BookEditionInputTest {

    @Mock
    private FilePart cover;

    @Test
    void exposesEditionAndCover() {
        BookEditionInput input = new BookEditionInput("First", cover);

        assertThat(input.edition()).isEqualTo("First");
        assertThat(input.cover()).isSameAs(cover);
    }

    @Test
    void convertsToDetailsWithCoverResource() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        when(cover.filename()).thenReturn("cover.png");
        when(cover.headers()).thenReturn(headers);
        BookEditionInput input = new BookEditionInput("First", cover);

        BookDetails details = input.toDetails();

        assertThat(details.edition()).isEqualTo("First");
        assertThat(details.resources())
            .containsEntry(ResourceSlug.COVER_IMAGE, new ResourceAttributes("cover.png", MediaType.IMAGE_PNG));
    }

    @Test
    void convertsToDetailsWithoutCoverResourceWhenCoverIsNull() {
        BookEditionInput input = new BookEditionInput("First", null);

        BookDetails details = input.toDetails();

        assertThat(details.edition()).isEqualTo("First");
        assertThat(details.resources()).isEmpty();
    }
}
