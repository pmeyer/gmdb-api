package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.PublicationIndexService;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.PublicationService;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.FileService;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MutationController {

    private final FileService fileService;
    private final PubMutationMapper mapper;
    private final PublicationService publicationService;
    private final PublicationIndexService pubIndexService;

    @MutationMapping("addTranscription")
    public Mono<Object> addTranscription(@Argument("pubId") Long pubId, @Valid @Argument("transcriptionInput") final TranscriptionInput input) {


        return Mono.just(input);
    }

    @MutationMapping("addMagazineIssue")
    public Mono<PubSearchResult> addMagazineIssue(@Argument("magInput") @Valid final MagazineInput magInput) {
        return publicationService.addPub(magInput);
    }

    @MutationMapping("addBookEdition")
    public Mono<PubSearchResult> addBookEdition(@Argument("bookInput") @Valid final BookInput bookInput) {
        return publicationService.addPub(bookInput);
    }

    @MutationMapping("addPubCoverImage")
    public Mono<PubSearchResult> addPubCoverImage(@Argument("imgInput") final PubCoverImageInput imgInput) {
        return mapper.updatePubCoverImage(imgInput.id(), imgInput.toDetails())
                .flatMap(r -> fileService
                        .put(imgInput.cover(), ResourceSlug.COVER_IMAGE, Map.of("id", r.details().resourceId()))
                        .thenReturn(r));
    }

    @MutationMapping("upsertPubIndex")
    public Mono<PubIndexOut> upsertPubIndex(@Valid @Argument("pubIndexInput") final PubIndexInput pubIndexInput) {
        return pubIndexService.upsertPublicationIndex(pubIndexInput);
    }
}

