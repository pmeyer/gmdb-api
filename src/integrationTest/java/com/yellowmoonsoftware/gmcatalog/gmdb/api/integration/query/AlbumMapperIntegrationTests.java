package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.AlbumMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Set;

class AlbumMapperIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

    @Autowired
    private AlbumMapper albumMapper;

    @Test
    void getAlbumsByIdsReturnsEmptyResultForEmptyIds() {
        StepVerifier.create(albumMapper.getAlbumsByIds(Set.of()))
                .verifyComplete();
    }

    @Test
    void getAlbumsFromIdsReturnsEmptyResultForEmptyIds() {
        StepVerifier.create(albumMapper.getAlbumsFromIds(Set.of()))
                .verifyComplete();
    }
}
