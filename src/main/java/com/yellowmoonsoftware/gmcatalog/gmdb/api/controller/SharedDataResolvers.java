package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.HasAlbumPrimaryArtistId;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.ArtistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class SharedDataResolvers {
    private final ArtistMapper artistMapper;

    /**
     * Resolves the artist for any album container holding a primary artist id.
     * @param artistIdContainers set of any container holding a primary artist id
     * @return map of the container to the artist data
     * @param <T> type of the container
     */
    public <T extends HasAlbumPrimaryArtistId> Mono<Map<T, ArtistOut>> artistsForAlbumArtistIdContainer(final Set<T> artistIdContainers) {
        final Map<Long, List<T>> artistAlbumMap = artistIdContainers.stream()
                .filter(a -> Objects.nonNull(a.primaryArtistId()))
                .collect(groupingBy(HasAlbumPrimaryArtistId::primaryArtistId));

        return artistMapper.getArtistsByIds(artistAlbumMap.keySet())
                .collectMap(ArtistOut::id)
                .map(m -> m.entrySet()
                        .stream()
                        .flatMap(e -> artistAlbumMap.get(e.getKey())
                                .stream()
                                .map(a -> Tuples.of(a, e.getValue())))
                        .collect(toMap(Tuple2::getT1, Tuple2::getT2)));
    }
}
