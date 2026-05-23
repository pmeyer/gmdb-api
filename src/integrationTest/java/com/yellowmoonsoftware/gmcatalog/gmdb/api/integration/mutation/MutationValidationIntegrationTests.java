package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class MutationValidationIntegrationTests extends GmdbGraphQlMutationIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedMutationDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerMutationIntegrationProperties(registry, DATABASE);
    }

    @Test
    void upsertPubIndexRejectsInputWithoutIdOrData() {
        assertValidationError("""
                mutation {
                    upsertPubIndex(pubIndexInput: { }) {
                        id
                    }
                }
                """, "PubIndexInput must have an ID or data");
    }

    @Test
    void addTranscriptionRejectsSongWithoutIdOrData() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: { }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "SongInput must have an ID or data");
    }

    @Test
    void addTranscriptionRejectsTranscriberWithoutIdOrName() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long songId = songIdByTitleAndAlbum("Rocket Queen", "Appetite For Destruction");

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: { id: %d }
                            pageNumber: 12
                            transcribers: [{ }]
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songId), "TranscriberInput must have an ID or data (or both)");
    }

    @Test
    void addTranscriptionRejectsSongArtistWithoutIdOrData() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "Negative Test Song Artist"
                                    artists: [{ roles: [PERFORMED_BY] }]
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "SongArtistInput must have an ID or data");
    }

    @Test
    void addTranscriptionRejectsAlbumWithoutIdOrData() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "Negative Test Album"
                                    albumTrack: {
                                        trackNumber: 1
                                        album: { }
                                    }
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "AlbumInput must have an ID or data");
    }

    @Test
    void addTranscriptionRejectsAlbumPrimaryArtistWithoutIdOrData() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "Negative Test Album Artist"
                                    albumTrack: {
                                        trackNumber: 1
                                        album: {
                                            data: {
                                                title: "Negative Test Album Artist Album"
                                                primaryArtist: { }
                                            }
                                        }
                                    }
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "ArtistInput must have an ID or data");
    }

    @Test
    void addMagazineIssueRejectsPublicationIndexWithoutIdOrData() {
        assertValidationError("""
                mutation {
                    addMagazineIssue(
                        magInput: {
                            pubDate: "2025-01-01"
                            index: { }
                            info: {
                                issueName: "Negative Test Magazine"
                            }
                        }
                    ) {
                        id
                    }
                }
                """, "PubIndexInput must have an ID or data");
    }

    @Test
    void addMagazineIssueRejectsBookPublicationIndex() {
        final long bookIndexId = pubIndexIdBySerialNumber("0898987660");

        assertValidationError("""
                mutation {
                    addMagazineIssue(
                        magInput: {
                            pubDate: "2025-01-01"
                            index: { id: %d }
                            info: {
                                issueName: "Negative Test Magazine"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(bookIndexId), "Pub type mismatch: input is for pub type MAG, but pub index specified is for pub type BOOK.");
    }

    @Test
    void addMagazineIssueRollsBackNewBookPublicationIndexWhenTypeDoesNotMatchOperation() {
        final String serialNumber = "NEG-BOOK-AS-MAG-001";

        assertValidationError("""
                mutation {
                    addMagazineIssue(
                        magInput: {
                            pubDate: "2025-01-01"
                            index: {
                                data: {
                                    name: "Negative Test Book Index"
                                    type: BOOK
                                    serial: "%s"
                                }
                            }
                            info: {
                                issueName: "Negative Test Magazine"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(serialNumber), "Pub type mismatch: input is for pub type MAG, but pub index specified is for pub type BOOK.");
        assertThat(countPubIndicesBySerialNumber(serialNumber)).isZero();
    }

    @Test
    void addBookEditionRejectsPublicationIndexWithoutIdOrData() {
        assertValidationError("""
                mutation {
                    addBookEdition(
                        bookInput: {
                            pubDate: "2025-01-01"
                            index: { }
                            info: {
                                edition: "Negative Test Edition"
                            }
                        }
                    ) {
                        id
                    }
                }
                """, "PubIndexInput must have an ID or data");
    }

    @Test
    void addBookEditionRejectsMagazinePublicationIndex() {
        final long magazineIndexId = pubIndexIdBySerialNumber("10456295");

        assertValidationError("""
                mutation {
                    addBookEdition(
                        bookInput: {
                            pubDate: "2025-01-01"
                            index: { id: %d }
                            info: {
                                edition: "Negative Test Edition"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(magazineIndexId), "Pub type mismatch: input is for pub type BOOK, but pub index specified is for pub type MAG.");
    }

    @Test
    void addBookEditionRollsBackNewMagazinePublicationIndexWhenTypeDoesNotMatchOperation() {
        final String serialNumber = "NEG-MAG-AS-BOOK-001";

        assertValidationError("""
                mutation {
                    addBookEdition(
                        bookInput: {
                            pubDate: "2025-01-01"
                            index: {
                                data: {
                                    name: "Negative Test Magazine Index"
                                    type: MAG
                                    serial: "%s"
                                }
                            }
                            info: {
                                edition: "Negative Test Edition"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(serialNumber), "Pub type mismatch: input is for pub type BOOK, but pub index specified is for pub type MAG.");
        assertThat(countPubIndicesBySerialNumber(serialNumber)).isZero();
    }

    private void assertValidationError(final String document, final String message) {
        graphQlTester.document(document)
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors)
                        .anySatisfy(error -> {
                            assertThat(error.getErrorType().toString()).isEqualTo("ValidationError");
                            assertThat(error.getMessage()).contains(message);
                        }));
    }

    private static long pubIdForGuitarWorldNovember2018() {
        return queryForLong(DATABASE, """
                select id
                from gmdb.pub
                where pub_idx_id = (
                    select id
                    from gmdb.pub_idx
                    where name = 'Guitar World'
                        and serial_number = '10456295'
                )
                    and pub_date = '2018-11-01'
                """);
    }

    private static long songIdByTitleAndAlbum(final String title, final String albumTitle) {
        return queryForLong(DATABASE, """
                select s.id
                from gmdb.song s
                    join gmdb.album alb on alb.id = s.album_id
                where s.title = '%s'
                    and alb.title = '%s'
                """.formatted(title, albumTitle));
    }

    private static long pubIndexIdBySerialNumber(final String serialNumber) {
        return queryForLong(DATABASE, """
                select id
                from gmdb.pub_idx
                where serial_number = '%s'
                """.formatted(serialNumber));
    }

    private static int countPubIndicesBySerialNumber(final String serialNumber) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub_idx
                where serial_number = '%s'
                """.formatted(serialNumber));
    }
}
