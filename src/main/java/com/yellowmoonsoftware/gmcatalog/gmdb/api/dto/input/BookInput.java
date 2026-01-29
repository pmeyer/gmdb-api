package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;

import java.time.LocalDate;
import java.util.List;

public class BookInput extends AbstractPubInput<BookEditionInput, BookDetails> {
    @JsonCreator
    public BookInput(final LocalDate pubDate,
                     final PubIndexInput index,
                     final BookEditionInput info,
                     final List<TranscriptionInput> transcriptions) {
        super(pubDate, index, info, transcriptions);
    }

    @Override
    public PubType supportedPubType() {
        return PubType.BOOK;
    }
}
