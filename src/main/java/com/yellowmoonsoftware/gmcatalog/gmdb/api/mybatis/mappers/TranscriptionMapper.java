package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;

@Mapper
public interface TranscriptionMapper {
    Mono<TranscriptionInOut> upsertTranscription(@Param("transcription") final TranscriptionInOut transcription);
    Mono<TranscriptionInOut> getTranscriptionById(@Param("id") final Long id);
}
