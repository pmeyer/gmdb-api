package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.AbstractResourceDetailsConverter;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

import java.util.Map;
import java.util.function.Supplier;

@Accessors(fluent = true)
public abstract class AbstractPubSpecificInput<T extends PubDetails> extends AbstractResourceDetailsConverter<T> {
    private final FilePart cover;

    public AbstractPubSpecificInput(final FilePart cover) {
        super(ResourceSlug.COVER_IMAGE, cover);
        this.cover = cover;
    }

    public FilePart cover() { return cover; }
}

