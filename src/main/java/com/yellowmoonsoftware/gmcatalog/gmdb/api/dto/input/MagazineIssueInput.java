package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.MagDetails;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

@Getter
@Accessors(fluent = true)
public class MagazineIssueInput extends AbstractPubSpecificInput<MagDetails> {
    private final String volume;
    private final String issue;
    @NotNull
    private final String issueName;

    @Accessors(fluent = false)
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final MagDetails details = new MagDetails(volume, issue, issueName);

    @JsonCreator
    public MagazineIssueInput(final String volume, final String issue, @NotNull final String issueName, final FilePart cover) {
        super(cover);
        this.volume = volume;
        this.issue = issue;
        this.issueName = issueName;
    }
}
