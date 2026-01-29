package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.MagDetails;

import java.time.LocalDate;
import java.util.List;

public class MagazineInput extends AbstractPubInput<MagazineIssueInput, MagDetails> {
    @JsonCreator
    public MagazineInput(final LocalDate pubDate,
                         final PubIndexInput index,
                         final MagazineIssueInput info,
                         final List<TranscriptionInput> transcriptions) {
        super(pubDate, index, info, transcriptions);
    }

    @Override
    public PubType supportedPubType() {
        return PubType.MAG;
    }
}

