package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Mapper
public interface TranscriptionMapper {
    Mono<TranscriptionInOut> upsertTranscription(@Param("transcription") final TranscriptionInOut transcription);
    Mono<TranscriptionInOut> getTranscriptionById(@Param("id") final Long id);
    Flux<TranscriptionInOut> getSongTranscriptionBySongIds(@Param("songIds") final Set<Long> songIds);
    Flux<TranscriptionInOut> getSongTranscriptionsByPubIds(@Param("pubIds") final Set<Long> pubIds);
}
