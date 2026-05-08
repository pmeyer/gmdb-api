package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.BookEditionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.BookInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriptionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InputValidationException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.GMDBMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

    @Mock
    private GMDBMapper gmdbMapper;

    @Mock
    private PubMutationMapper pubMutationMapper;

    @Mock
    private PublicationIndexService pubIndexService;

    @Mock
    private FileService fileService;

    @Mock
    private TranscriptionService transcriptionService;

    @Mock
    private FilePart cover;

    @InjectMocks
    private PublicationService publicationService;

    @Test
    void addPubStoresCoverTranscriptionsAndReturnsPublication() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(cover.filename()).thenReturn("cover.jpg");
        when(cover.headers()).thenReturn(headers);
        PubIndexInput index = new PubIndexInput(1L, null);
        TranscriptionInput transcription = new TranscriptionInput(new SongInput(2L, null), 12, null, List.of());
        BookInput input = new BookInput(LocalDate.of(2024, 1, 15), index, new BookEditionInput("First", cover), List.of(transcription));
        PubIndexOut pubIndex = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        PubOut pubOut = new PubOut(10L, input.pubDate(), 1L, bookDetails(), null);
        PubSearchResult result = new PubSearchResult(10L, "Guide", PubType.BOOK, pubOut.details(), input.pubDate(), "ISBN-1", 1L);
        when(pubIndexService.upsertPublicationIndex(index)).thenReturn(Mono.just(pubIndex));
        when(pubMutationMapper.upsertPublication(any())).thenReturn(Mono.just(pubOut));
        when(fileService.put(cover, ResourceSlug.COVER_IMAGE, Map.of("id", pubOut.details().resourceId())))
            .thenReturn(Mono.just(new ResourceReference(ResourceSlug.COVER_IMAGE, "pub/10", "cover.jpg")));
        when(transcriptionService.upsertTranscription(10L, transcription))
            .thenReturn(Mono.just(new TranscriptionInOut(20L, 30L, 10L, null, null)));
        when(gmdbMapper.getPub(10L)).thenReturn(Mono.just(result));

        StepVerifier.create(publicationService.addPub(input))
            .expectNext(result)
            .verifyComplete();

        verify(pubIndexService).upsertPublicationIndex(index);
        verify(pubMutationMapper).upsertPublication(any());
        verify(fileService).put(cover, ResourceSlug.COVER_IMAGE, Map.of("id", pubOut.details().resourceId()));
        verify(transcriptionService).upsertTranscription(10L, transcription);
        verify(gmdbMapper).getPub(10L);
        verifyNoMoreInteractions(pubIndexService, pubMutationMapper, fileService, transcriptionService, gmdbMapper);
    }

    @Test
    void addPubRejectsMismatchedPublicationType() {
        PubIndexInput index = new PubIndexInput(1L, null);
        BookInput input = new BookInput(LocalDate.of(2024, 1, 15), index, new BookEditionInput("First", null), List.of());
        when(pubIndexService.upsertPublicationIndex(index))
            .thenReturn(Mono.just(new PubIndexOut(1L, "Magazine", PubType.MAG, "ISSN-1")));

        StepVerifier.create(publicationService.addPub(input))
            .expectErrorSatisfies(error -> assertThat(error)
                .isInstanceOf(InputValidationException.class)
                .hasMessageContaining("Pub type mismatch"))
            .verify();

        verify(pubIndexService).upsertPublicationIndex(index);
        verifyNoMoreInteractions(pubIndexService);
        verifyNoInteractions(pubMutationMapper, fileService, transcriptionService, gmdbMapper);
    }

    @Test
    void validationHelperCanBeConstructed() {
        assertThat(new PublicationService.Validation()).isNotNull();
    }

    private static BookDetails bookDetails() {
        return new BookDetails("First") {
            @Override
            public UUID resourceId() {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };
    }
}
