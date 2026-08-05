package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriptionData;
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
                .then(Mono.defer(() -> upsertTranscriptionForPublication(pubId, input)));
    }

    private Mono<TranscriptionInOut> upsertTranscriptionForPublication(
            final Long pubId,
            final TranscriptionInput input) {

        final Mono<TranscriptionInOut> upsertSignal = input.mode() == IdAndDataContainer.DataMode.REF
                ? Mono.defer(() -> transcriptionMapper.getTranscriptionById(input.id()))
                    .flatMap(existing -> includeTranscription(pubId, existing))
                : upsertTranscriptionData(pubId, input);

        return input.id() == null
                ? upsertSignal
                : transcriptionMapper.getTranscriptionId(input.id())
                    .switchIfEmpty(Mono.error(new InvalidInputException("Unknown transcription ID: " + input.id())))
                    .then(upsertSignal);
    }

    private Mono<TranscriptionInOut> includeTranscription(final Long pubId, final TranscriptionInOut existing) {
        return transcriptionMapper.upsertTranscription(new TranscriptionInOut(
                existing.id(), existing.songId(), pubId, existing.details(), null));
    }

    private Mono<TranscriptionInOut> upsertTranscriptionData(final Long pubId, final TranscriptionInput input) {
        final TranscriptionData data = input.data();
        return songService.upsertSong(data.song())
                .flatMap(song -> transcriptionMapper.upsertTranscription(new TranscriptionInOut(
                        input.id(), song.id(), pubId, data.toDetails(), null)))
                .flatMap(tOut -> {
                    final Mono<ResourceReference> fileSignal = Mono.justOrEmpty(data.file())
                            .flatMap(blob -> fileService.put(blob, ResourceSlug.TRANSCRIPTION,
                                    Map.of("id", tOut.details().resourceId())));

                    final Mono<Void> transcribersSignal = transcriptionTranscriberService
                            .addTranscriptionTranscribers(tOut.id(), data.transcribers());

                    return Mono.when(fileSignal, transcribersSignal)
                            .thenReturn(tOut);
                });
    }
}
