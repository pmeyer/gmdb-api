package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongArtistIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongArtistInput;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongArtistService {
    private final ArtistService artistService;

    @Transactional
    public Mono<Void> addSongArtists(final Long songId, final List<SongArtistInput> songArtists) {
        return Flux.fromStream(Streams.of(songArtists))
                .flatMap(sa -> {
                    if (sa.mode() == IdAndDataContainer.DataMode.REF) {
                        return artistService.validateArtistId(sa.id())
                                .map(artistId -> new SongArtistIn(songId,
                                        artistId,
                                        sa.roles().toArray(new SongArtistRole[0])));
                    }
                    return artistService.upsertArtist(new ArtistInput(sa.id(), sa.data()))
                            .map(a -> new SongArtistIn(songId, a.id(), sa.roles().toArray(new SongArtistRole[0])));
                }, 4)
                .collectList()
                .filter(l -> !l.isEmpty())
                .flatMapMany(artistService::upsertSongArtists)
                .then();
    }
}
