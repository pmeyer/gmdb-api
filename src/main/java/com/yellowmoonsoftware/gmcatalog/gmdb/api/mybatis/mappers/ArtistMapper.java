package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import org.apache.ibatis.annotations.Mapper;
import reactor.core.publisher.Mono;

@Mapper
public interface ArtistMapper {
    Mono<ArtistOut> getArtistById(final Long id);
    Mono<ArtistOut> upsertArtist(final ArtistInput input);
}
