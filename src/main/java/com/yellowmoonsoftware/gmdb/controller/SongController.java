package com.yellowmoonsoftware.gmdb.controller;

import com.yellowmoonsoftware.gmdb.dto.output.SongAlbum;
import com.yellowmoonsoftware.gmdb.dto.output.SongArtist;
import com.yellowmoonsoftware.gmdb.dto.output.SongSearchResult;
import com.yellowmoonsoftware.gmdb.dto.output.Transcription;
import com.yellowmoonsoftware.gmdb.mappers.DataResolversMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yellowmoonsoftware.gmdb.util.ReactiveUtils.async;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Controller
@RequiredArgsConstructor
public class SongController {
    private final DataResolversMapper mapper;

    @BatchMapping(typeName = "Song", field = "album")
    public Mono<Map<SongSearchResult, SongAlbum>> albumForSong(final Set<SongSearchResult> songs) {
        return commonAlbumFetch(songs);
    }

    @BatchMapping(typeName = "SongSearchResult", field = "album")
    public Mono<Map<SongSearchResult, SongAlbum>> albumForSongSearchResult(final Set<SongSearchResult> songs) {
        return commonAlbumFetch(songs);
    }

    private Mono<Map<SongSearchResult, SongAlbum>> commonAlbumFetch(final Set<SongSearchResult> songs) {
        return async(() -> {
            final Map<Long, List<SongSearchResult>> albumSongMap = songs.stream()
                    .collect(groupingBy(SongSearchResult::albumId));
            return mapper.getAlbumsFromIds(albumSongMap.keySet())
                    .entrySet()
                    .stream()
                    .flatMap(e -> albumSongMap.get(e.getKey())
                            .stream()
                            .map(s -> Tuples.of(s, new SongAlbum(e.getValue(), s))))
                    .collect(toMap(Tuple2::getT1, Tuple2::getT2));
        });
    }

    @BatchMapping(typeName = "Song", field = "artists")
    public Mono<Map<SongSearchResult, List<SongArtist>>> artistsForSong(final Set<SongSearchResult> songs) {
        return commonArtistFetch(songs);
    }

    @BatchMapping(typeName = "SongSearchResult", field = "artists")
    public Mono<Map<SongSearchResult, List<SongArtist>>> artistsForSongSearchResult(final Set<SongSearchResult> songs) {
        return commonArtistFetch(songs);
    }

    private Mono<Map<SongSearchResult, List<SongArtist>>> commonArtistFetch(final Set<SongSearchResult> songs) {
        return async(() -> {
            Map<Long, SongSearchResult> songMap = songs.stream()
                    .collect(toMap(SongSearchResult::id, s -> s));

            return mapper.getSongArtistBySongIds(songMap.keySet())
                    .stream()
                    .collect(groupingBy(a -> songMap.get(a.songId())));
        });
    }

    @BatchMapping(typeName = "SongSearchResult", field = "transcriptions")
    public Mono<Map<SongSearchResult, List<Transcription>>> transcriptions(final Set<SongSearchResult> songs) {
        return async(() -> {
            Map<Long, SongSearchResult> songMap = songs.stream()
                    .collect(toMap(SongSearchResult::id, s -> s));

            return mapper.getSongTranscriptionBySongIds(songMap.keySet())
                    .stream()
                    .collect(groupingBy(a -> songMap.get(a.songId())));
        });
    }
}

