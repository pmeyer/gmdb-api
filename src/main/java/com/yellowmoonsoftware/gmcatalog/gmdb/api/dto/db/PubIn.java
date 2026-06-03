package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AbstractPubInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import java.time.LocalDate;

public record PubIn(Long id,
                    LocalDate pubDate,
                    Long pubIdxId,
                    PubDetails details) {
    static public PubIn from(final Long pubIdxId, final AbstractPubInput<?, ? extends PubDetails> input) {
        return new PubIn(input.id(), input.pubDate(), pubIdxId, input.info().toDetails());
    }
}
