package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.BookEditionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.BookInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.MagazineInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.MagazineIssueInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubCoverImageInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriptionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.FileService;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.PublicationIndexService;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.PublicationService;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceReference;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.TranscriptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MutationControllerTest {

    @Mock
    private FileService fileService;

    @Mock
    private PubMapper pubMapper;

    @Mock
    private PubMutationMapper mapper;

    @Mock
    private PublicationService publicationService;

    @Mock
    private PublicationIndexService pubIndexService;

    @Mock
    private TranscriptionService transcriptionService;

    @Mock
    private FilePart cover;

    @InjectMocks
    private MutationController mutationController;

    @Test
    void addTranscriptionMapsServiceOutputToGraphQlTranscription() {
        final TranscriptionInput input = new TranscriptionInput(new SongInput(1L, null), 12, null, List.of());
        final TranscriptionInOut output = new TranscriptionInOut(2L, 1L, 3L, new TranscriptionDetails(12), null);
        when(transcriptionService.upsertTranscription(3L, input)).thenReturn(Mono.just(output));

        StepVerifier.create(mutationController.addTranscription(3L, input))
            .assertNext(result -> {
                assertThat(result.id()).isEqualTo(2L);
                assertThat(result.pageNumber()).isEqualTo(12);
                assertThat(result.songId()).isEqualTo(1L);
                assertThat(result.pubId()).isEqualTo(3L);
            })
            .verifyComplete();

        verify(transcriptionService).upsertTranscription(3L, input);
    }

    @Test
    void addMagazineIssueDelegatesToPublicationService() {
        final MagazineInput input = new MagazineInput(LocalDate.of(2024, 1, 15), new PubIndexInput(1L, null), new MagazineIssueInput("12", "4", "Winter", null), List.of());
        final PubSearchResult output = pubSearchResult();
        when(publicationService.addPub(input)).thenReturn(Mono.just(output));

        StepVerifier.create(mutationController.addMagazineIssue(input)).expectNext(output).verifyComplete();

        verify(publicationService).addPub(input);
    }

    @Test
    void addBookEditionDelegatesToPublicationService() {
        final BookInput input = new BookInput(LocalDate.of(2024, 1, 15), new PubIndexInput(1L, null), new BookEditionInput("First", null), List.of());
        final PubSearchResult output = pubSearchResult();
        when(publicationService.addPub(input)).thenReturn(Mono.just(output));

        StepVerifier.create(mutationController.addBookEdition(input)).expectNext(output).verifyComplete();

        verify(publicationService).addPub(input);
    }

    @Test
    void addPubCoverImageUpdatesDetailsAndStoresFile() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(cover.filename()).thenReturn("cover.jpg");
        when(cover.headers()).thenReturn(headers);
        final PubCoverImageInput input = new PubCoverImageInput(1L, cover);
        final PubSearchResult output = pubSearchResult();
        when(pubMapper.getPubId(1L)).thenReturn(Mono.just(1L));
        when(mapper.updatePubCoverImage(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(Mono.just(output));
        when(fileService.put(cover, ResourceSlug.COVER_IMAGE, Map.of("id", output.details().resourceId())))
            .thenReturn(Mono.just(new ResourceReference(ResourceSlug.COVER_IMAGE, "pub/1", "cover.jpg")));

        StepVerifier.create(mutationController.addPubCoverImage(input)).expectNext(output).verifyComplete();

        verify(pubMapper).getPubId(1L);
        verify(mapper).updatePubCoverImage(org.mockito.ArgumentMatchers.eq(1L), any());
        verify(fileService).put(cover, ResourceSlug.COVER_IMAGE, Map.of("id", output.details().resourceId()));
    }

    @Test
    void addPubCoverImageRejectsUnknownPublicationIdBeforeUpdatingDetailsOrStoringFile() {
        final PubCoverImageInput input = new PubCoverImageInput(1L, cover);
        when(pubMapper.getPubId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(mutationController.addPubCoverImage(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown publication ID: 1");
                })
                .verify();

        verify(pubMapper).getPubId(1L);
        verifyNoInteractions(mapper, fileService);
    }

    @Test
    void upsertPubIndexDelegatesToPublicationIndexService() {
        final PubIndexInput input = new PubIndexInput(1L, null);
        final PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(pubIndexService.upsertPublicationIndex(input)).thenReturn(Mono.just(output));

        StepVerifier.create(mutationController.upsertPubIndex(input)).expectNext(output).verifyComplete();

        verify(pubIndexService).upsertPublicationIndex(input);
    }

    private static PubSearchResult pubSearchResult() {
        final BookDetails details = new BookDetails("First") {
            @Override
            public UUID resourceId() {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };
        return new PubSearchResult(1L, "Guide", PubType.BOOK, details, LocalDate.of(2024, 1, 15), "ISBN-1", 2L);
    }
}
