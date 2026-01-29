package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.SongMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {
    private final AlbumService albumService;
    private final SongArtistService songArtistService;
    private final SongMapper songMapper;

    public record AlbumTrack(AlbumOut album, int trackNumber) { }

    @Transactional
    public Mono<SongOut> upsertSong(final SongInput input) {
        if (input.mode() == IdAndDataContainer.DataMode.REF) {
            return songMapper.getSongById(input.id());
        }

        final Mono<Optional<AlbumTrack>> albumTrackSignal = Mono.justOrEmpty(input.data().albumTrack())
                .flatMap(albumTrack -> albumService
                        .upsertAlbum(albumTrack.album())
                        .map(albumOut -> new AlbumTrack(albumOut, albumTrack.trackNumber())))
                .singleOptional();

        return albumTrackSignal
                .flatMap(opt -> {
                    final SongIn dbSongIn = new SongIn(input.id(),
                            input.data().title(),
                            opt.map(t -> new SongDetails(t.trackNumber())).orElse(null),
                            opt.map(t -> t.album().id()).orElse(null));
                    return songMapper.upsertSong(dbSongIn);
                })
                .flatMap(songOut -> songArtistService.addSongArtists(songOut.id(), input.data().artists())
                        .thenReturn(songOut));
    }
}


