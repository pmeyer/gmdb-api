package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourcesContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Mapper
public interface PubMutationMapper {
    Mono<PubSearchResult> addPub(@Param("pubType") PubType pubType, @Param("pubDate") LocalDate pubDate, @Param("pubDetails") final PubDetails pubDetails, @Param("pubIdx") final PubIndexInput pubIdx);
    Mono<PubSearchResult> updatePubCoverImage(@Param("id") Long id, @Param("cover") final ResourcesContainer cover);
}
