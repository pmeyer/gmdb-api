package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.MagDetails;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

@Getter
@Accessors(fluent = true)
public class MagazineIssueInput extends AbstractPubSpecificInput<MagDetails> {
    private final String volume;
    private final String issue;
    private final String issueName;

    @Getter(lazy = true)
    @Accessors(fluent = false)
    private final MagDetails details = new MagDetails(volume, issue, issueName);

    @JsonCreator
    public MagazineIssueInput(final String volume, final String issue, final String issueName, final FilePart cover) {
        super(cover);
        this.volume = volume;
        this.issue = issue;
        this.issueName = issueName;
    }
}

