package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.MagDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.springframework.http.codec.multipart.FilePart;

public record MagazineIssueInput(
        String volume,
        String issue,
        String issueName,
        FilePart cover) implements PubSpecificInput<MagDetails> {

    public MagDetails toDetails() {
        final MagDetails magDetails = new MagDetails(volume, issue, issueName);
        if (cover != null) {
            magDetails.addResource(ResourceSlug.COVER_IMAGE, cover);
        }
        return magDetails;
    }
}