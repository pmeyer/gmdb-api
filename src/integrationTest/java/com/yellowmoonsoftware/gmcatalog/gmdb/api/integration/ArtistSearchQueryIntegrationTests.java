package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Set;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole.ALBUM_ARTIST;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole.MUSIC_BY;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole.PERFORMED_BY;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole.WORDS_BY;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType.BAND;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType.PERSON;
import static org.assertj.core.api.Assertions.assertThat;

class ArtistSearchQueryIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @BeforeAll
    static void applyTestData() {
        applyBaselineTestData(DATABASE);
    }

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerGmdbIntegrationProperties(registry, DATABASE);
    }

    @Test
    void artistSearchWithoutCriteriaReturnsExpectedArtists() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch {
                                id
                                name
                                type
                                matchedRoles
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistSearchResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                artist(4, "Barbara Logan", PERSON, MUSIC_BY, WORDS_BY),
                artist(6, "Brad Wilk", PERSON, MUSIC_BY, WORDS_BY),
                artist(8, "Cinderella", BAND, ALBUM_ARTIST, PERFORMED_BY),
                artist(9, "Dave Grohl", PERSON, MUSIC_BY, WORDS_BY),
                artist(10, "David A. Stewart", PERSON, MUSIC_BY, WORDS_BY),
                artist(11, "Doyle Bramhall", PERSON, MUSIC_BY, WORDS_BY),
                artist(12, "Duff McKagan", PERSON, MUSIC_BY, WORDS_BY),
                artist(14, "Foo Fighters", BAND, ALBUM_ARTIST, PERFORMED_BY),
                artist(16, "Guns N' Roses", BAND, ALBUM_ARTIST, PERFORMED_BY),
                artist(17, "Izzy Stradlin", PERSON, MUSIC_BY, WORDS_BY),
                artist(18, "Jeff Lyne", PERSON, MUSIC_BY, WORDS_BY),
                artist(20, "John Keene", PERSON, MUSIC_BY, WORDS_BY),
                artist(23, "Mike Campbell", PERSON, MUSIC_BY, WORDS_BY),
                artist(27, "Pete Townshend", PERSON, MUSIC_BY, WORDS_BY),
                artist(31, "Rage Against The Machine", BAND, ALBUM_ARTIST, PERFORMED_BY),
                artist(36, "Slash", PERSON, MUSIC_BY, WORDS_BY),
                artist(39, "Steven Adler", PERSON, MUSIC_BY, WORDS_BY),
                artist(40, "Stevie Ray Vaughan", PERSON, ALBUM_ARTIST, PERFORMED_BY),
                artist(43, "The Who", BAND, ALBUM_ARTIST, PERFORMED_BY),
                artist(44, "Tim Commerford", PERSON, MUSIC_BY, WORDS_BY),
                artist(45, "Tom Keifer", PERSON, MUSIC_BY, WORDS_BY),
                artist(46, "Tom Morello", PERSON, MUSIC_BY, WORDS_BY),
                artist(47, "Tom Petty", PERSON, MUSIC_BY, WORDS_BY),
                artist(48, "Tom Petty & the Heartbreakers", BAND, ALBUM_ARTIST, PERFORMED_BY),
                artist(50, "W. Axl Rose", PERSON, MUSIC_BY, WORDS_BY),
                artist(52, "Zack de la Rocha", PERSON, MUSIC_BY, WORDS_BY));
    }

    private static ArtistSearchResponse artist(
            final long id,
            final String name,
            final ArtistType type,
            final ArtistSearchRole... matchedRoles) {

        return new ArtistSearchResponse(id, name, type, Set.of(matchedRoles));
    }

    private record ArtistSearchResponse(
            Long id,
            String name,
            ArtistType type,
            Set<ArtistSearchRole> matchedRoles) {
    }
}
