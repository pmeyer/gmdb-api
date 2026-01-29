package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

@Getter
@Accessors(fluent = true)
public class BookEditionInput extends AbstractPubSpecificInput<BookDetails> {
    private final String edition;

    @Getter(lazy = true)
    @Accessors(fluent = false)
    private final BookDetails details = new BookDetails(edition);

    @JsonCreator
    public BookEditionInput(final String edition, final FilePart cover) {
        super(cover);
        this.edition = edition;
    }
}
