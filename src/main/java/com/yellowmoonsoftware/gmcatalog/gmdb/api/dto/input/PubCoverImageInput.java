package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import org.springframework.http.codec.multipart.FilePart;

public record PubCoverImageInput(
        Long id,
        FilePart cover
) { }
