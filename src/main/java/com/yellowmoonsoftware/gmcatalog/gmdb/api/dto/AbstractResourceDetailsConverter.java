package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class AbstractResourceDetailsConverter<T extends ResourceBundle> implements ResourceDetailsConverter<T> {

    abstract protected Map<ResourceSlug, Supplier<FilePart>> getResources();

    abstract protected T getDetails();

    @Override
    public T toDetails() {
        final T details = getDetails();
        getResources().entrySet().stream()
                .filter(e -> Objects.nonNull(e.getValue().get()))
                .forEach(e -> details.addResource(e.getKey(), e.getValue().get()));
        return details;
    }
}
