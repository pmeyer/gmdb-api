package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriberOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcriber;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Mapper
public interface TranscriberMapper {
    Mono<TranscriberOut> upsertTranscriber(@Param("input") final TranscriberInput input);
    Flux<Transcriber> getTranscribers(@Param("searchName") final String searchName);
    Mono<Long> getTranscriberId(@Param("id") final Long id);
    Mono<TranscriberOut> getTranscriberById(@Param("id") final Long id);
    Flux<TranscriptionTranscriber> upsertTranscriptionTranscribers(@Param("transcriptionTranscribers") final List<TranscriptionTranscriber> transcriptionTranscribers);
    Flux<com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.TranscriptionTranscriber> getTranscribersByTranscriptionIds(@Param("transcriptionIds") final Set<Long> transcriptionIds);
    Mono<Integer> clearTranscriptionTranscribers(@Param("transcriptionId") final Long transcriptionId);
}
