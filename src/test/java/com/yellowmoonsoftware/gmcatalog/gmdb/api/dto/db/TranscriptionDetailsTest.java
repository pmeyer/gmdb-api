package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptionDetailsTest {

    @Test
    void exposesPageNumberAndEmptyResourceState() {
        TranscriptionDetails details = new TranscriptionDetails(12);

        assertThat(details.pageNumber()).isEqualTo(12);
        assertThat(details.resourceId()).isNull();
        assertThat(details.resources()).isEmpty();
        assertThat(details.transcriptionUrl()).isNull();
    }

    @Test
    void transcriptionUrlUsesTranscriptionResourceWhenPresent() {
        TranscriptionDetails details = new TranscriptionDetails(12);
        details.resources().put(ResourceSlug.TRANSCRIPTION, new ResourceAttributes("page.pdf", MediaType.APPLICATION_PDF));

        assertThat(details.transcriptionUrl())
            .contains("/resources/transcription/")
            .contains("/transcription")
            .contains("_f=page.pdf")
            .contains("_mt=application/pdf");
    }
}
