package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.ArtistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistMapper artistMapper;

    public Mono<ArtistOut> upsertArtist(final ArtistInput input) {
        return input.mode() != IdAndDataContainer.DataMode.REF
                ? artistMapper.upsertArtist(input)
                : artistMapper.getArtistById(input.id());
    }
}
