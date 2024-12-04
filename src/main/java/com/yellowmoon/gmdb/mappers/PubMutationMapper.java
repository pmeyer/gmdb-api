package com.yellowmoon.gmdb.mappers;

import com.yellowmoon.gmdb.dto.PubType;
import com.yellowmoon.gmdb.dto.input.PubIndexInput;
import com.yellowmoon.gmdb.dto.output.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Mapper
public interface PubMutationMapper {
    PubSearchResult addPub(@Param("pubType") PubType pubType, @Param("pubDate") LocalDate pubDate, @Param("pubDetails") final PubDetails pubDetails, @Param("pubIdx") final PubIndexInput pubIdx);
    PubSearchResult updatePubCoverImage(@Param("id") Long id, @Param("cover") final String cover);
}
