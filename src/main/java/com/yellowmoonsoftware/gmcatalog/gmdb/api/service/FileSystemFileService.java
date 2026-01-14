package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.util.ReactiveUtils.async;

@Service
public class FileSystemFileService implements FileService {

    private final String fileRoot;

    public FileSystemFileService(@Value("${file-service.root}") final String fileRoot, @Value("${user.home}") final String userHome) {
        this.fileRoot = fileRoot.replaceFirst("^~" + File.separator, userHome + File.separator);
    }

    @Override
    public Mono<ResourceReference> put(final FilePart filePart, final ResourceSlug slug, final Map<String, ?> slugVariables) {
        final String slugPath = slug.getPath(slugVariables);

        final File destination = Path.of(this.fileRoot).resolve(slugPath).toFile();
        return async(() -> FileUtils.createParentDirectories(destination))
                .then(filePart.transferTo(destination))
                .thenReturn(new ResourceReference(slug, slugPath, filePart.filename()));
    }

    @Override
    public Flux<DataBuffer> get(ResourceSlug slug, Map<String, ?> slugVariables) {
        final String slugPath = slug.getPath(slugVariables);

        final Path path = Path.of(this.fileRoot).resolve(slugPath);
        if (!PathUtils.isRegularFile(path)) {
            return Flux.error(new FileNotFoundException("File resource not found: " + path));
        }

        return DataBufferUtils.read(path, new DefaultDataBufferFactory(), 4096);
    }

}

