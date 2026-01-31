package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractResourceDetailsConverter<T extends ResourceBundle> implements ResourceDetailsConverter<T> {
    @Getter(value = AccessLevel.PROTECTED)
    private final Map<ResourceSlug, Supplier<FilePart>> resources;

    abstract protected T getDetails();

    protected AbstractResourceDetailsConverter(final ResourceSlug slugs, final FilePart blob) {
        resources = Map.of(slugs, () -> blob);
    }

    @Override
    public T toDetails() {
        final T details = getDetails();
        getResources().entrySet().stream()
                .filter(e -> Objects.nonNull(e.getValue().get()))
                .forEach(e -> details.addResource(e.getKey(), e.getValue().get()));
        return details;
    }
}
