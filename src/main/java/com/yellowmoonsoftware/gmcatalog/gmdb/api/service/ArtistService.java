package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.ArtistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistMapper artistMapper;

    public Mono<ArtistOut> upsertArtist(final ArtistInput input) {
        final Mono<ArtistOut> upsertSignal = input.mode() != IdAndDataContainer.DataMode.REF
                ? Mono.defer(() -> artistMapper.upsertArtist(input))
                : Mono.defer(() -> artistMapper.getArtistById(input.id()));

        return input.id() == null
                ? upsertSignal
                : validateArtistId(input.id()).then(upsertSignal);
    }

    public Mono<Long> validateArtistId(final Long id) {
        return artistMapper.getArtistId(id)
                .switchIfEmpty(Mono.error(new InvalidInputException("Unknown artist ID: " + id)));
    }

    public Flux<SongArtistOut> upsertSongArtists(final List<SongArtistIn> songArtists) {
        return artistMapper.upsertSongArtists(songArtists);
    }

}
