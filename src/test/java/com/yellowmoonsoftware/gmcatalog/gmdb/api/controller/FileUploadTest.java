package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FileUploadTest {

    @Mock
    private FilePart filePart;

    @Test
    void exposesFileUploadValues() {
        final FileUpload upload = new FileUpload("cover", filePart);

        assertThat(upload.type()).isEqualTo("cover");
        assertThat(upload.file()).isSameAs(filePart);
    }

    @Test
    void supportsRecordEqualityHashCodeAndToString() {
        final FileUpload upload = new FileUpload("cover", filePart);
        final FileUpload same = new FileUpload("cover", filePart);
        final FileUpload different = new FileUpload("transcription", filePart);

        assertThat(upload)
                .isEqualTo(same)
                .hasSameHashCodeAs(same)
                .isNotEqualTo(different)
                .hasToString("FileUpload[type=cover, file=" + filePart + "]");
    }
}
