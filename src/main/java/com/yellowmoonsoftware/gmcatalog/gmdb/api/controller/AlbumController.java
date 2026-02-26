package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AlbumController {
    private final SharedDataResolvers sharedDataResolvers;

    @BatchMapping(typeName = "AlbumSearchResult", field = "artist")
    public Mono<Map<AlbumSearchResult, ArtistOut>> artistForAlbum(final Set<AlbumSearchResult> albums) {
        return sharedDataResolvers.artistsForAlbumArtistIdContainer(albums);
    }
}
