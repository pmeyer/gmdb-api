package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourcesContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubSearchCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.TranscriptionPublication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Set;

@Mapper
public interface PubMapper {
    Mono<Long> getPubId(@Param("id") final Long id);
    Mono<PubSearchResult> getPub(@Param("pubId") final Long pubId);
    Flux<PubSearchResult> pubSearch(@Param("criteria") final PubSearchCriteria criteria);
    Mono<PubSearchResult> addPub(@Param("pubType") PubType pubType, @Param("pubDate") LocalDate pubDate, @Param("pubDetails") final PubDetails pubDetails, @Param("pubIdx") final PubIndexInput pubIdx);
    Mono<PubSearchResult> updatePubCoverImage(@Param("id") Long id, @Param("cover") final ResourcesContainer cover);
    Mono<PubOut> upsertPublication(@Param("pubIn") final PubIn pubIn);
    Flux<TranscriptionPublication> getPublicationByTranscriptionIds(@Param("transcriptionIds") final Set<Long> transcriptionIds);
}
