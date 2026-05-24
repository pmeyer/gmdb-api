package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriptionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranscriptionService {
    private final SongService songService;
    private final TranscriptionTranscriberService transcriptionTranscriberService;
    private final FileService fileService;
    private final PubMapper pubMapper;
    private final TranscriptionMapper transcriptionMapper;

    @Transactional
    public Mono<TranscriptionInOut> upsertTranscription(final Long pubId, final TranscriptionInput input) {
        return pubMapper.getPubId(pubId)
                .switchIfEmpty(Mono.error(new InvalidInputException("Unknown publication ID: " + pubId)))
                .then(Mono.defer(() -> songService.upsertSong(input.song())))
                .flatMap(s -> transcriptionMapper
                        .upsertTranscription(TranscriptionInOut.forNewTranscription(s.id(), pubId, input.toDetails())))
                .flatMap(tOut -> {
                    final Mono<ResourceReference> fileSignal = Mono.justOrEmpty(input.file())
                            .flatMap(blob -> fileService.put(blob, ResourceSlug.TRANSCRIPTION,
                                    Map.of("id", tOut.details().resourceId())));

                    final Mono<Void> transcribersSignal = transcriptionTranscriberService
                            .addTranscriptionTranscribers(tOut.id(), input.transcribers());

                    return Mono.when(fileSignal, transcribersSignal)
                            .thenReturn(tOut);
                });
    }
}
