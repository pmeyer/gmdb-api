package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.MagDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import org.springframework.http.codec.multipart.FilePart;

public record MagazineIssueInput(
        String volume,
        String issue,
        String issueName,
        FilePart cover) implements PubSpecificInput<MagDetails> {

    public MagDetails toDetails() {
        return new MagDetails(cover.filename(), volume, issue, issueName);
    }
}