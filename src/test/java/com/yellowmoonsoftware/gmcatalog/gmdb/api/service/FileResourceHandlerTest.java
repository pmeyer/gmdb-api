package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileResourceHandlerTest {

    @Mock
    private FileService fileService;

    @Mock
    private ServerRequest request;

    @InjectMocks
    private FileResourceHandler handler;

    @Test
    void getFileUsesFilenameAndExplicitMediaTypeQueryParameters() {
        final Map<String, String> variables = Map.of("slug", "cover-img", "id", "abc");
        when(request.queryParam("_f")).thenReturn(Optional.of("cover.bin"));
        when(request.queryParam("_mt")).thenReturn(Optional.of("image/png"));
        when(request.pathVariable("slug")).thenReturn("cover-img");
        when(request.pathVariables()).thenReturn(variables);
        when(fileService.get(ResourceSlug.COVER_IMAGE, variables)).thenReturn(Flux.empty());

        StepVerifier.create(handler.getFile(request))
            .assertNext(response -> {
                assertThat(response.statusCode()).isEqualTo(ServerResponse.ok().build().block().statusCode());
                assertThat(response.headers().getContentDisposition().getFilename()).isEqualTo("cover.bin");
                assertThat(response.headers().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
            })
            .verifyComplete();

        verify(fileService).get(ResourceSlug.COVER_IMAGE, variables);
    }

    @Test
    void getFileInfersMediaTypeFromFilenameWhenExplicitMediaTypeMissing() {
        final Map<String, String> variables = Map.of("slug", "album-art", "id", "abc");
        when(request.queryParam("_f")).thenReturn(Optional.of("cover.jpg"));
        when(request.queryParam("_mt")).thenReturn(Optional.empty());
        when(request.pathVariable("slug")).thenReturn("album-art");
        when(request.pathVariables()).thenReturn(variables);
        when(fileService.get(ResourceSlug.ALBUM_ART, variables)).thenReturn(Flux.empty());

        StepVerifier.create(handler.getFile(request))
            .assertNext(response -> {
                assertThat(response.headers().getContentDisposition().getFilename()).isEqualTo("cover.jpg");
                assertThat(response.headers().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
            })
            .verifyComplete();

        verify(fileService).get(ResourceSlug.ALBUM_ART, variables);
    }

    @Test
    void getFileAllowsMissingQueryParameters() {
        final Map<String, String> variables = Map.of("slug", "transcription", "id", "abc");
        when(request.queryParam("_f")).thenReturn(Optional.empty());
        when(request.queryParam("_mt")).thenReturn(Optional.empty());
        when(request.pathVariable("slug")).thenReturn("transcription");
        when(request.pathVariables()).thenReturn(variables);
        when(fileService.get(ResourceSlug.TRANSCRIPTION, variables)).thenReturn(Flux.empty());

        StepVerifier.create(handler.getFile(request))
            .assertNext(response -> {
                assertThat(response.headers().getContentDisposition().getType()).isEmpty();
                assertThat(response.headers().getContentDisposition().getFilename()).isNull();
                assertThat(response.headers().getContentType()).isNull();
            })
            .verifyComplete();

        verify(fileService).get(ResourceSlug.TRANSCRIPTION, variables);
    }
}
