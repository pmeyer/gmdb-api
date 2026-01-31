package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriberOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Mapper
public interface TranscriberMapper {
    Mono<TranscriberOut> upsertTranscriber(@Param("input") final TranscriberInput input);
    Mono<TranscriberOut> getTranscriberById(@Param("id") final Long id);
    Flux<TranscriptionTranscriber> upsertTranscriptionTranscribers(@Param("transcriptionTranscribers") final List<TranscriptionTranscriber> transcriptionTranscribers);
}
