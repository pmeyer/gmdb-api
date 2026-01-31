package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongArtistInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongArtistService {
    private final ArtistService artistService;

    @Transactional
    public Mono<Void> addSongArtists(final Long songId, final List<SongArtistInput> songArtists) {
        return Flux.fromIterable(Optional.ofNullable(songArtists).orElse(List.of()))
                .flatMap(sa -> {
                    if (sa.mode() == IdAndDataContainer.DataMode.REF) {
                        return Mono.just(new SongArtistIn(songId, sa.id(), sa.roles().toArray(new SongArtistRole[0])));
                    }
                    return artistService.upsertArtist(new ArtistInput(sa.id(), sa.data()))
                            .map(a -> new SongArtistIn(songId, a.id(), sa.roles().toArray(new SongArtistRole[0])));
                }, 4)
                .collectList()
                .flatMapMany(artistService::upsertSongArtists)
                .then();
    }
}
