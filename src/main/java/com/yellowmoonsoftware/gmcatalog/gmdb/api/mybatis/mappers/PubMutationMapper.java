package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourcesContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Mapper
public interface PubMutationMapper {
    Mono<PubIndexOut> upsertPubIndex(@Param("pubIdx") final PubIndexInput pubIdx);
    Mono<PubSearchResult> addPub(@Param("pubType") PubType pubType, @Param("pubDate") LocalDate pubDate, @Param("pubDetails") final PubDetails pubDetails, @Param("pubIdx") final PubIndexInput pubIdx);
    Mono<PubSearchResult> updatePubCoverImage(@Param("id") Long id, @Param("cover") final ResourcesContainer cover);
    Mono<PubOut> upsertPublication(@Param("pubIn") final PubIn pubIn);
}

