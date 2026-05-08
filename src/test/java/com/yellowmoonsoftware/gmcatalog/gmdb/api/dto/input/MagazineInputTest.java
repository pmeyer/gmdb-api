package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MagazineInputTest {

    @Test
    void exposesInheritedPublicationFieldsAndSupportedType() {
        LocalDate pubDate = LocalDate.of(2024, 1, 15);
        PubIndexInput index = new PubIndexInput(1L, null);
        MagazineIssueInput info = new MagazineIssueInput("12", "4", "Winter", null);
        List<TranscriptionInput> transcriptions = List.of(new TranscriptionInput(new SongInput(2L, null), 12, null, List.of()));

        MagazineInput input = new MagazineInput(pubDate, index, info, transcriptions);

        assertThat(input.pubDate()).isEqualTo(pubDate);
        assertThat(input.index()).isSameAs(index);
        assertThat(input.info()).isSameAs(info);
        assertThat(input.transcriptions()).isSameAs(transcriptions);
        assertThat(input.supportedPubType()).isEqualTo(PubType.MAG);
    }
}
