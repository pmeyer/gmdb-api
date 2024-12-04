package com.yellowmoon.gmdb.dto.input;

import org.springframework.http.codec.multipart.FilePart;

public record PubCoverImageInput(
        Long id,
        FilePart cover
) { }
