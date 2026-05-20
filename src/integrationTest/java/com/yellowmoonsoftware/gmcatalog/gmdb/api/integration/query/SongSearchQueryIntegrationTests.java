package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SongSearchQueryIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

    @Autowired
    private WebGraphQlTester graphQlTester;

    @Test
    void songSearchWithoutCriteriaReturnsExpectedSongs() {
        final var results = graphQlTester.document("""
                        query {
                            songSearch {
                                title
                            }
                        }
                        """)
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrderElementsOf(allExpectedSongTitles());
    }

    @Test
    void songSearchWithTitleSearchMatchesSongTitleCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: { titleSearch: "girl" }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                songTitle("American Girl"),
                songTitle("Here Comes My Girl"));
    }

    @Test
    void songSearchWithPubNameSearchMatchesPublicationNameCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: { pubNameSearch: "practicing" }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                songTitle("Gypsy Road"));
    }

    @Test
    void songSearchWithArtistSearchMatchesAnyAssociatedSongArtistCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: { artistSearch: "morello" }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                songTitle("Bombtrack"));
    }

    @Test
    void songSearchWithAlbumSearchMatchesAlbumTitleCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: { albumSearch: "colour" }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                songTitle("Everlong"));
    }

    @Test
    void songSearchWithAlbumsReturnsSongsAppearingOnSuppliedAlbums() {
        final var greatestHitsId = queryForInt(DATABASE, """
                select id
                from gmdb.album
                where title = 'Greatest Hits'
                """);

        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: { albums: [%d] }) {
                                title
                            }
                        }
                        """.formatted(greatestHitsId))
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                songTitle("American Girl"),
                songTitle("Breakdown"),
                songTitle("Don't Come Around Here No More"),
                songTitle("Don't Do Me Like That"),
                songTitle("Even The Losers"),
                songTitle("Free Fallin'"),
                songTitle("Here Comes My Girl"),
                songTitle("I Need To Know"),
                songTitle("I Won't Back Down"),
                songTitle("Into The Great Wide Open"),
                songTitle("Learning to Fly"),
                songTitle("Listen To Her Heart"),
                songTitle("Mary Jane's Last Dance"),
                songTitle("Refugee"),
                songTitle("Runnin' Down a Dream"),
                songTitle("Something In The Air"),
                songTitle("The Waiting"),
                songTitle("You Got Lucky"));
    }

    @Test
    void songSearchWithPubsReturnsSongsAppearingInSuppliedPublications() {
        final var guitarWorldNovember2018Id = queryForInt(DATABASE, """
                select pub.id
                from gmdb.pub pub
                    inner join gmdb.pub_idx pub_idx on pub.pub_idx_id = pub_idx.id
                where pub_idx.name = 'Guitar World'
                    and pub.details->>'issueName' = 'November 2018'
                """);

        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: { pubs: [%d] }) {
                                title
                            }
                        }
                        """.formatted(guitarWorldNovember2018Id))
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                songTitle("Bombtrack"),
                songTitle("Rocket Queen"));
    }

    @Test
    void songSearchWithArtistsReturnsSongsMatchingSuppliedArtistAndSongRole() {
        final var tomPettyId = queryForInt(DATABASE, """
                select id
                from gmdb.artist
                where name = 'Tom Petty'
                """);

        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: {
                                artists: [
                                    { id: %d, roles: [MUSIC_BY] }
                                ]
                            }) {
                                title
                            }
                        }
                        """.formatted(tomPettyId))
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                songTitle("American Girl"),
                songTitle("Breakdown"),
                songTitle("Don't Come Around Here No More"),
                songTitle("Don't Do Me Like That"),
                songTitle("Even The Losers"),
                songTitle("Free Fallin'"),
                songTitle("Here Comes My Girl"),
                songTitle("I Need To Know"),
                songTitle("I Won't Back Down"),
                songTitle("Into The Great Wide Open"),
                songTitle("Learning to Fly"),
                songTitle("Listen To Her Heart"),
                songTitle("Mary Jane's Last Dance"),
                songTitle("Refugee"),
                songTitle("Runnin' Down a Dream"),
                songTitle("The Waiting"),
                songTitle("You Got Lucky"));
    }

    @Test
    void songSearchWithArtistsReturnsSongsMatchingSuppliedAlbumArtistRole() {
        final var fooFightersId = queryForInt(DATABASE, """
                select id
                from gmdb.artist
                where name = 'Foo Fighters'
                """);

        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: {
                                artists: [
                                    { id: %d, roles: [ALBUM_ARTIST] }
                                ]
                            }) {
                                title
                            }
                        }
                        """.formatted(fooFightersId))
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                songTitle("Everlong"));
    }

    @Test
    void songSearchWithArtistsDoesNotFilterByRoleWhenRolesAreOmitted() {
        final var tomPettyAndTheHeartbreakersId = queryForInt(DATABASE, """
                select id
                from gmdb.artist
                where name = 'Tom Petty & the Heartbreakers'
                """);

        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: {
                                artists: [
                                    { id: %d }
                                ]
                            }) {
                                title
                            }
                        }
                        """.formatted(tomPettyAndTheHeartbreakersId))
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrderElementsOf(tomPettyGreatestHitsSongTitles());
    }

    @Test
    void songSearchWithArtistsDoesNotFilterByRoleWhenRolesAreEmpty() {
        final var tomPettyAndTheHeartbreakersId = queryForInt(DATABASE, """
                select id
                from gmdb.artist
                where name = 'Tom Petty & the Heartbreakers'
                """);

        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: {
                                artists: [
                                    { id: %d, roles: [] }
                                ]
                            }) {
                                title
                            }
                        }
                        """.formatted(tomPettyAndTheHeartbreakersId))
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrderElementsOf(tomPettyGreatestHitsSongTitles());
    }

    @Test
    void songSearchCombinesCriteriaElements() {
        final var greatestHitsId = queryForInt(DATABASE, """
                select id
                from gmdb.album
                where title = 'Greatest Hits'
                """);

        final var tomPettyId = queryForInt(DATABASE, """
                select id
                from gmdb.artist
                where name = 'Tom Petty'
                """);

        final var results = graphQlTester.document("""
                        query {
                            songSearch(criteria: {
                                titleSearch: "run",
                                albumSearch: "greatest",
                                albums: [%d],
                                artists: [
                                    { id: %d, roles: [MUSIC_BY] }
                                ]
                            }) {
                                title
                            }
                        }
                        """.formatted(greatestHitsId, tomPettyId))
                .execute()
                .path("songSearch")
                .entityList(SongTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                songTitle("Runnin' Down a Dream"));
    }

    private static Set<SongTitleResponse> allExpectedSongTitles() {
        return Set.of(
                songTitle("American Girl"),
                songTitle("Bombtrack"),
                songTitle("Breakdown"),
                songTitle("Don't Come Around Here No More"),
                songTitle("Don't Do Me Like That"),
                songTitle("Even The Losers"),
                songTitle("Everlong"),
                songTitle("Free Fallin'"),
                songTitle("Gypsy Road"),
                songTitle("Here Comes My Girl"),
                songTitle("I Need To Know"),
                songTitle("I Won't Back Down"),
                songTitle("Into The Great Wide Open"),
                songTitle("Learning to Fly"),
                songTitle("Life By The Drop"),
                songTitle("Listen To Her Heart"),
                songTitle("Mary Jane's Last Dance"),
                songTitle("Refugee"),
                songTitle("Rocket Queen"),
                songTitle("Runnin' Down a Dream"),
                songTitle("Something In The Air"),
                songTitle("Substitute"),
                songTitle("The Waiting"),
                songTitle("You Got Lucky"));
    }

    private static Set<SongTitleResponse> tomPettyGreatestHitsSongTitles() {
        return Set.of(
                songTitle("American Girl"),
                songTitle("Breakdown"),
                songTitle("Don't Come Around Here No More"),
                songTitle("Don't Do Me Like That"),
                songTitle("Even The Losers"),
                songTitle("Free Fallin'"),
                songTitle("Here Comes My Girl"),
                songTitle("I Need To Know"),
                songTitle("I Won't Back Down"),
                songTitle("Into The Great Wide Open"),
                songTitle("Learning to Fly"),
                songTitle("Listen To Her Heart"),
                songTitle("Mary Jane's Last Dance"),
                songTitle("Refugee"),
                songTitle("Runnin' Down a Dream"),
                songTitle("Something In The Air"),
                songTitle("The Waiting"),
                songTitle("You Got Lucky"));
    }

    private static SongTitleResponse songTitle(final String title) {
        return new SongTitleResponse(title);
    }

    private record SongTitleResponse(String title) {
    }
}
