package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceContainerImpl;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourcesContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.springframework.http.codec.multipart.FilePart;

public record PubCoverImageInput(
        Long id,
        FilePart cover
) {

    public ResourcesContainer toDetails() {
        return new ResourceContainerImpl() {{
            addResource(ResourceSlug.COVER_IMAGE, cover);
        }};
    }
}
