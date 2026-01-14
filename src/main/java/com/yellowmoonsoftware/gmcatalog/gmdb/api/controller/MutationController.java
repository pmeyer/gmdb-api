package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.FileService;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
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

    @MutationMapping("addTranscription")
    public Mono<Object> addTranscription(@Argument("transcriptionInput") final GqlInputTypes.TranscriptionInput input) {


        return Mono.just(input);
    }

    @MutationMapping("addMagazineIssue")
    public Mono<PubSearchResult> addMagazineIssue(@Argument("magInput") final MagazineInput magInput) {
        // validation
        return validate(magInput)
                .flatMap(i -> {
                    return mapper.addPub(PubType.MAG, i.pubDate(), i.issueInfo().toDetails(), i.index())
                            .flatMap(pubSearchResult -> {
                                return fileService.put(i.issueInfo().cover(), ResourceSlug.COVER_IMAGE,
                                                Map.of("id", pubSearchResult.id()))
                                        .thenReturn(pubSearchResult);
                            });
                });
    }

    @MutationMapping("addPubCoverImage")
    public Mono<PubSearchResult> addPubCoverImage(@Argument("imgInput") final PubCoverImageInput imgInput) {
        return mapper.updatePubCoverImage(imgInput.id(), imgInput.cover().filename())
                .flatMap(r -> fileService
                        .put(imgInput.cover(), ResourceSlug.COVER_IMAGE, Map.of("id", r.id()))
                        .thenReturn(r));
    }

    private static Mono<MagazineInput> validate(final MagazineInput magInput) {
        final PubIndexInput index = magInput.index();
        if (index.flag() == PubIndexInputFlag.LOOKUP) {
            if (index.id() == null && index.serial() == null) {
                return Mono.error(new RuntimeException("PubIndex lookup mode, at least one of ID, serial number or name is required."));
            }
        } else {
            if (index.name() == null) {
                return Mono.error(new RuntimeException("PubIndex upsert mode, name is required."));
            }
        }

        return Mono.just(magInput);
    }
}

