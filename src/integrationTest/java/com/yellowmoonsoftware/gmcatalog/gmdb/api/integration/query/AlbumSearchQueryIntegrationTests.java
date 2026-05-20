package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumSearchQueryIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

    @Autowired
    private WebGraphQlTester graphQlTester;

    @Test
    void albumSearchWithoutCriteriaReturnsExpectedAlbums() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch {
                                title
                                releaseDate
                                artist {
                                    name
                                }
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                album("Appetite For Destruction", "1987-07-21", "Guns N' Roses"),
                album("Rage Against The Machine", "1992-11-03", "Rage Against The Machine"),
                album("The Sky Is Crying", "1991-11-05", "Stevie Ray Vaughan"),
                album("Long Cold Winter", "1988-05-21", "Cinderella"),
                album("Greatest Hits", "1993-11-16", "Tom Petty & the Heartbreakers"),
                album("Meaty Beaty Big and Bouncy", "1971-10-30", "The Who"),
                album("The Colour and the Shape", "1997-05-20", "Foo Fighters"),
                album("Lost Dogs", "2003-11-11", "Pearl Jam"),
                album("Who's Next", null, "The Who"));
    }

    @Test
    void albumSearchWithSearchNameMatchesAlbumTitleCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: { searchName: "cOlOuR" }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                albumTitle("The Colour and the Shape"));
    }

    @Test
    void albumSearchWithSearchNameMatchesAlbumArtistNameCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: { searchName: "the who" }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumTitleResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                albumTitle("Meaty Beaty Big and Bouncy"),
                albumTitle("Who's Next"));
    }

    @Test
    void albumSearchWithDateStartReturnsAlbumsReleasedOnOrAfterDate() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: { dateStart: "1993-11-16" }) {
                                title
                                releaseDate
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumReleaseDateResponse.class)
                .get();

        assertThat(datedAlbums(results)).containsExactlyInAnyOrder(
                albumReleaseDate("Greatest Hits", "1993-11-16"),
                albumReleaseDate("The Colour and the Shape", "1997-05-20"),
                albumReleaseDate("Lost Dogs", "2003-11-11"));
    }

    @Test
    void albumSearchWithDateEndReturnsAlbumsReleasedOnOrBeforeDate() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: { dateEnd: "1988-05-21" }) {
                                title
                                releaseDate
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumReleaseDateResponse.class)
                .get();

        assertThat(datedAlbums(results)).containsExactlyInAnyOrder(
                albumReleaseDate("Meaty Beaty Big and Bouncy", "1971-10-30"),
                albumReleaseDate("Appetite For Destruction", "1987-07-21"),
                albumReleaseDate("Long Cold Winter", "1988-05-21"));
    }

    @Test
    void albumSearchWithDateStartAndDateEndReturnsAlbumsReleasedWithinInclusiveRange() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: { dateStart: "1991-11-05", dateEnd: "1992-11-03" }) {
                                title
                                releaseDate
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumReleaseDateResponse.class)
                .get();

        assertThat(datedAlbums(results)).containsExactlyInAnyOrder(
                albumReleaseDate("The Sky Is Crying", "1991-11-05"),
                albumReleaseDate("Rage Against The Machine", "1992-11-03"));
    }

    @Test
    void albumSearchOrdersResultsByTitle() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: { orderBy: [{ column: TITLE, direction: ASC }] }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                albumTitle("Appetite For Destruction"),
                albumTitle("Greatest Hits"),
                albumTitle("Long Cold Winter"),
                albumTitle("Lost Dogs"),
                albumTitle("Meaty Beaty Big and Bouncy"),
                albumTitle("Rage Against The Machine"),
                albumTitle("The Colour and the Shape"),
                albumTitle("The Sky Is Crying"),
                albumTitle("Who's Next"));
    }

    @Test
    void albumSearchOrdersResultsByReleaseDate() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: {
                                orderBy: [
                                    { column: RELEASE_DATE, direction: DESC, nullsOrder: NULLS_LAST }
                                ]
                            }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                albumTitle("Lost Dogs"),
                albumTitle("The Colour and the Shape"),
                albumTitle("Greatest Hits"),
                albumTitle("Rage Against The Machine"),
                albumTitle("The Sky Is Crying"),
                albumTitle("Long Cold Winter"),
                albumTitle("Appetite For Destruction"),
                albumTitle("Meaty Beaty Big and Bouncy"),
                albumTitle("Who's Next"));
    }

    @Test
    void albumSearchOrdersResultsByArtist() {
        final var results = graphQlTester.document("""
                        query {
                            albumSearch(criteria: {
                                orderBy: [
                                    { column: ARTIST, direction: ASC },
                                    { column: TITLE, direction: ASC }
                                ]
                            }) {
                                title
                            }
                        }
                        """)
                .execute()
                .path("albumSearch")
                .entityList(AlbumTitleResponse.class)
                .get();

        assertThat(results).containsExactly(
                albumTitle("Long Cold Winter"),
                albumTitle("The Colour and the Shape"),
                albumTitle("Appetite For Destruction"),
                albumTitle("Lost Dogs"),
                albumTitle("Rage Against The Machine"),
                albumTitle("The Sky Is Crying"),
                albumTitle("Meaty Beaty Big and Bouncy"),
                albumTitle("Who's Next"),
                albumTitle("Greatest Hits"));
    }

    private static List<AlbumReleaseDateResponse> datedAlbums(final List<AlbumReleaseDateResponse> albums) {
        return albums.stream()
                .filter(album -> album.releaseDate() != null)
                .toList();
    }

    private static AlbumResponse album(
            final String title,
            final String releaseDate,
            final String artistName) {

        return new AlbumResponse(
                title,
                releaseDate == null ? null : LocalDate.parse(releaseDate),
                new ArtistResponse(artistName));
    }

    private static AlbumTitleResponse albumTitle(final String title) {
        return new AlbumTitleResponse(title);
    }

    private static AlbumReleaseDateResponse albumReleaseDate(
            final String title,
            final String releaseDate) {

        return new AlbumReleaseDateResponse(title, LocalDate.parse(releaseDate));
    }

    private record AlbumResponse(String title, LocalDate releaseDate, ArtistResponse artist) {
    }

    private record AlbumTitleResponse(String title) {
    }

    private record AlbumReleaseDateResponse(String title, LocalDate releaseDate) {
    }

    private record ArtistResponse(String name) {
    }
}
