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

    @Test
    void artistSearchWithSearchNameMatchesArtistNameCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: { searchName: "tom" }) {
                                name
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistNameResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                artistName("Tom Keifer"),
                artistName("Tom Morello"),
                artistName("Tom Petty"),
                artistName("Tom Petty & the Heartbreakers"));
    }

    @Test
    void artistSearchWithTypeReturnsArtistsOfThatType() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: { type: BAND }) {
                                name
                                type
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistTypeResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                artistType("Cinderella", BAND),
                artistType("Foo Fighters", BAND),
                artistType("Guns N' Roses", BAND),
                artistType("Rage Against The Machine", BAND),
                artistType("The Who", BAND),
                artistType("Tom Petty & the Heartbreakers", BAND));
    }

    @Test
    void artistSearchWithSingleRoleReturnsArtistsMatchingThatRole() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: { roles: [ALBUM_ARTIST] }) {
                                name
                                matchedRoles
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistRolesResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                artistRoles("Cinderella", ALBUM_ARTIST),
                artistRoles("Foo Fighters", ALBUM_ARTIST),
                artistRoles("Guns N' Roses", ALBUM_ARTIST),
                artistRoles("Rage Against The Machine", ALBUM_ARTIST),
                artistRoles("Stevie Ray Vaughan", ALBUM_ARTIST),
                artistRoles("The Who", ALBUM_ARTIST),
                artistRoles("Tom Petty & the Heartbreakers", ALBUM_ARTIST));
    }

    @Test
    void artistSearchWithMultipleRolesReturnsArtistsMatchingAnyRole() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: { roles: [MUSIC_BY, PERFORMED_BY] }) {
                                name
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistNameResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrderElementsOf(allExpectedArtistNames());
    }

    @Test
    void artistSearchWithAllRolesReturnsSameArtistsAsNoRoleCriteria() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: { roles: [WORDS_BY, MUSIC_BY, PERFORMED_BY, ALBUM_ARTIST] }) {
                                name
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistNameResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrderElementsOf(allExpectedArtistNames());
    }

    @Test
    void artistSearchRestrictingToTranscribedArtistsOnlyReturnsArtistsWithTranscribedSongs() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: { restrictToTranscribedArtists: true }) {
                                name
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistNameResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrderElementsOf(allExpectedArtistNames());
    }

    @Test
    void artistSearchOrdersResultsByCriteria() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: {
                                roles: [ALBUM_ARTIST],
                                orderBy: [
                                    { column: NAME_TITLE_SORT, direction: DESC }
                                ]
                            }) {
                                name
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistNameResponse.class)
                .get();

        assertThat(results).containsExactly(
                artistName("The Who"),
                artistName("Tom Petty & the Heartbreakers"),
                artistName("Stevie Ray Vaughan"),
                artistName("Rage Against The Machine"),
                artistName("Guns N' Roses"),
                artistName("Foo Fighters"),
                artistName("Cinderella"));
    }

    @Test
    void artistSearchCombinesCriteriaElements() {
        final var results = graphQlTester.document("""
                        query {
                            artistSearch(criteria: {
                                searchName: "tom",
                                type: PERSON,
                                roles: [MUSIC_BY],
                                restrictToTranscribedArtists: true
                            }) {
                                name
                            }
                        }
                        """)
                .execute()
                .path("artistSearch")
                .entityList(ArtistNameResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                artistName("Tom Keifer"),
                artistName("Tom Morello"),
                artistName("Tom Petty"));
    }

    private static Set<ArtistNameResponse> allExpectedArtistNames() {
        return Set.of(
                artistName("Barbara Logan"),
                artistName("Brad Wilk"),
                artistName("Cinderella"),
                artistName("Dave Grohl"),
                artistName("David A. Stewart"),
                artistName("Doyle Bramhall"),
                artistName("Duff McKagan"),
                artistName("Foo Fighters"),
                artistName("Guns N' Roses"),
                artistName("Izzy Stradlin"),
                artistName("Jeff Lyne"),
                artistName("John Keene"),
                artistName("Mike Campbell"),
                artistName("Pete Townshend"),
                artistName("Rage Against The Machine"),
                artistName("Slash"),
                artistName("Steven Adler"),
                artistName("Stevie Ray Vaughan"),
                artistName("The Who"),
                artistName("Tim Commerford"),
                artistName("Tom Keifer"),
                artistName("Tom Morello"),
                artistName("Tom Petty"),
                artistName("Tom Petty & the Heartbreakers"),
                artistName("W. Axl Rose"),
                artistName("Zack de la Rocha"));
    }

    private static ArtistNameResponse artistName(final String name) {
        return new ArtistNameResponse(name);
    }

    private static ArtistTypeResponse artistType(final String name, final ArtistType type) {
        return new ArtistTypeResponse(name, type);
    }

    private static ArtistRolesResponse artistRoles(
            final String name,
            final ArtistSearchRole... matchedRoles) {

        return new ArtistRolesResponse(name, Set.of(matchedRoles));
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

    private record ArtistNameResponse(String name) {
    }

    private record ArtistTypeResponse(String name, ArtistType type) {
    }

    private record ArtistRolesResponse(String name, Set<ArtistSearchRole> matchedRoles) {
    }
}
