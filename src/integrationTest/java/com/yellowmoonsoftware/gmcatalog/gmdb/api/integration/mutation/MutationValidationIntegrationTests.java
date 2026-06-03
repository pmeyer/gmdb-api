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
    void upsertPubIndexRejectsUnknownReferenceId() {
        assertValidationError("""
                mutation {
                    upsertPubIndex(pubIndexInput: { id: 999999999 }) {
                        id
                    }
                }
                """, "Unknown publication index ID: 999999999");
    }

    @Test
    void upsertPubIndexRejectsUnknownIdAndDataInputBeforeUpsert() {
        final String serialNumber = "NEG-PUB-IDX-UNKNOWN-ID";

        assertValidationError("""
                mutation {
                    upsertPubIndex(pubIndexInput: {
                        id: 999999999
                        data: {
                            name: "Negative Test Unknown Pub Index ID"
                            type: BOOK
                            serial: "%s"
                        }
                    }) {
                        id
                    }
                }
                """.formatted(serialNumber), "Unknown publication index ID: 999999999");
        assertThat(countPubIndicesBySerialNumber(serialNumber)).isZero();
    }

    @Test
    void upsertPubIndexRejectsNullInput() {
        assertRequestError("""
                mutation {
                    upsertPubIndex(pubIndexInput: null) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void upsertPubIndexRejectsDataMissingRequiredSerialNumber() {
        assertRequestError("""
                mutation {
                    upsertPubIndex(pubIndexInput: {
                        data: {
                            name: "Negative Test Pub Index"
                            type: BOOK
                        }
                    }) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void upsertPubIndexRejectsDataMissingRequiredName() {
        assertRequestError("""
                mutation {
                    upsertPubIndex(pubIndexInput: {
                        data: {
                            type: BOOK
                            serial: "NEG-PUB-IDX-MISSING-NAME"
                        }
                    }) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void upsertPubIndexRejectsDataMissingRequiredType() {
        assertRequestError("""
                mutation {
                    upsertPubIndex(pubIndexInput: {
                        data: {
                            name: "Negative Test Pub Index Missing Type"
                            serial: "NEG-PUB-IDX-MISSING-TYPE"
                        }
                    }) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void addTranscriptionRejectsMissingRequiredPublicationIdArgument() {
        assertRequestError("""
                mutation {
                    addTranscription(
                        transcriptionInput: {
                            song: { id: 1 }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void addTranscriptionRejectsNullRequiredPageNumber() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: { id: 1 }
                            pageNumber: null
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addTranscriptionRejectsNullRequiredInput() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: null
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addTranscriptionRejectsNullRequiredSong() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: null
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addTranscriptionRejectsSongDataMissingRequiredTitle() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    artists: [{ id: 1, roles: [PERFORMED_BY] }]
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addTranscriptionRejectsSongArtistMissingRequiredRoles() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "Negative Test Missing Artist Roles"
                                    artists: [{ id: 1 }]
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addTranscriptionRejectsAlbumTrackMissingRequiredTrackNumber() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "Negative Test Missing Track Number"
                                    albumTrack: {
                                        album: { id: 1 }
                                    }
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addTranscriptionRejectsAlbumDataMissingRequiredTitle() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "Negative Test Missing Album Title"
                                    albumTrack: {
                                        trackNumber: 1
                                        album: {
                                            data: {
                                                releaseDate: "2025-01-01"
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
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addTranscriptionRejectsArtistDataMissingRequiredType() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertRequestError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "Negative Test Missing Artist Type"
                                    artists: [{
                                        data: {
                                            name: "Negative Test Artist Missing Type"
                                        }
                                        roles: [PERFORMED_BY]
                                    }]
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Validation error");
    }

    @Test
    void addMagazineIssueRejectsNullRequiredInput() {
        assertRequestError("""
                mutation {
                    addMagazineIssue(magInput: null) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void addMagazineIssueRejectsInfoMissingRequiredIssueName() {
        assertRequestError("""
                mutation {
                    addMagazineIssue(
                        magInput: {
                            pubDate: "2025-01-01"
                            index: { id: 1 }
                            info: { }
                        }
                    ) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void addBookEditionRejectsNullRequiredInput() {
        assertRequestError("""
                mutation {
                    addBookEdition(bookInput: null) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void addPubCoverImageRejectsNullRequiredInput() {
        assertRequestError("""
                mutation {
                    addPubCoverImage(imgInput: null) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void addPubCoverImageRejectsMissingRequiredId() {
        assertRequestError("""
                mutation {
                    addPubCoverImage(imgInput: {
                        cover: null
                    }) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void addPubCoverImageRejectsNullRequiredCover() {
        assertRequestError("""
                mutation {
                    addPubCoverImage(imgInput: {
                        id: 1
                        cover: null
                    }) {
                        id
                    }
                }
                """, "Validation error");
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
    void addTranscriptionRejectsUnknownSongReference() {
        final long pubId = pubIdForGuitarWorldNovember2018();

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: { id: 999999999 }
                            pageNumber: 991
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId), "Unknown song ID: 999999999");
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 991)).isZero();
    }

    @Test
    void addTranscriptionRejectsUnknownSongIdAndDataBeforeUpsert() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final String songTitle = "Negative Test Unknown Song ID";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                id: 999999999
                                data: {
                                    title: "%s"
                                }
                            }
                            pageNumber: 992
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songTitle), "Unknown song ID: 999999999");
        assertThat(countSongsByTitle(songTitle)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 992)).isZero();
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
    void addTranscriptionRejectsUnknownTranscriberReference() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long songId = songIdByTitleAndAlbum("Rocket Queen", "Appetite For Destruction");

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: { id: %d }
                            pageNumber: 999
                            transcribers: [{ id: 999999999 }]
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songId), "Unknown transcriber ID: 999999999");
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 999)).isZero();
    }

    @Test
    void addTranscriptionRejectsUnknownTranscriberIdAndDataBeforeUpsert() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long songId = songIdByTitleAndAlbum("Rocket Queen", "Appetite For Destruction");
        final String transcriberName = "Negative Test Unknown Transcriber ID";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: { id: %d }
                            pageNumber: 1000
                            transcribers: [{
                                id: 999999999
                                name: "%s"
                            }]
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songId, transcriberName), "Unknown transcriber ID: 999999999");
        assertThat(countTranscribersByName(transcriberName)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 1000)).isZero();
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
    void addTranscriptionRejectsUnknownSongArtistReference() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final String songTitle = "Negative Test Unknown Song Artist Reference";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "%s"
                                    artists: [{
                                        id: 999999999
                                        roles: [PERFORMED_BY]
                                    }]
                                }
                            }
                            pageNumber: 995
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songTitle), "Unknown artist ID: 999999999");
        assertThat(countSongsByTitle(songTitle)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 995)).isZero();
    }

    @Test
    void addTranscriptionRejectsUnknownSongArtistIdAndDataBeforeUpsert() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final String artistName = "Negative Test Unknown Song Artist ID";
        final String songTitle = "Negative Test Unknown Song Artist ID Song";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "%s"
                                    artists: [{
                                        id: 999999999
                                        data: {
                                            name: "%s"
                                            type: PERSON
                                        }
                                        roles: [PERFORMED_BY]
                                    }]
                                }
                            }
                            pageNumber: 996
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songTitle, artistName), "Unknown artist ID: 999999999");
        assertThat(countArtistsByName(artistName)).isZero();
        assertThat(countSongsByTitle(songTitle)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 996)).isZero();
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
    void addTranscriptionRejectsUnknownAlbumReference() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final String songTitle = "Negative Test Unknown Album Reference";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "%s"
                                    albumTrack: {
                                        trackNumber: 1
                                        album: { id: 999999999 }
                                    }
                                }
                            }
                            pageNumber: 993
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songTitle), "Unknown album ID: 999999999");
        assertThat(countSongsByTitle(songTitle)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 993)).isZero();
    }

    @Test
    void addTranscriptionRejectsUnknownAlbumIdAndDataBeforeUpsert() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final String albumTitle = "Negative Test Unknown Album ID";
        final String songTitle = "Negative Test Unknown Album ID Song";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "%s"
                                    albumTrack: {
                                        trackNumber: 1
                                        album: {
                                            id: 999999999
                                            data: {
                                                title: "%s"
                                            }
                                        }
                                    }
                                }
                            }
                            pageNumber: 994
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songTitle, albumTitle), "Unknown album ID: 999999999");
        assertThat(countAlbumsByTitle(albumTitle)).isZero();
        assertThat(countSongsByTitle(songTitle)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 994)).isZero();
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
    void addTranscriptionRejectsUnknownAlbumPrimaryArtistReference() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final String albumTitle = "Negative Test Unknown Album Primary Artist Reference";
        final String songTitle = "Negative Test Unknown Album Primary Artist Reference Song";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "%s"
                                    albumTrack: {
                                        trackNumber: 1
                                        album: {
                                            data: {
                                                title: "%s"
                                                primaryArtist: { id: 999999999 }
                                            }
                                        }
                                    }
                                }
                            }
                            pageNumber: 997
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songTitle, albumTitle), "Unknown artist ID: 999999999");
        assertThat(countAlbumsByTitle(albumTitle)).isZero();
        assertThat(countSongsByTitle(songTitle)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 997)).isZero();
    }

    @Test
    void addTranscriptionRejectsUnknownAlbumPrimaryArtistIdAndDataBeforeUpsert() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final String artistName = "Negative Test Unknown Album Primary Artist ID";
        final String albumTitle = "Negative Test Unknown Album Primary Artist ID";
        final String songTitle = "Negative Test Unknown Album Primary Artist ID Song";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: %d
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "%s"
                                    albumTrack: {
                                        trackNumber: 1
                                        album: {
                                            data: {
                                                title: "%s"
                                                primaryArtist: {
                                                    id: 999999999
                                                    data: {
                                                        name: "%s"
                                                        type: PERSON
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            pageNumber: 998
                        }
                    ) {
                        id
                    }
                }
                """.formatted(pubId, songTitle, albumTitle, artistName), "Unknown artist ID: 999999999");
        assertThat(countArtistsByName(artistName)).isZero();
        assertThat(countAlbumsByTitle(albumTitle)).isZero();
        assertThat(countSongsByTitle(songTitle)).isZero();
        assertThat(countTranscriptionsByPubIdAndPageNumber(pubId, 998)).isZero();
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
    void addMagazineIssueRejectsUnknownPublicationIndexReference() {
        final String issueName = "Negative Test Magazine Unknown Index";

        assertValidationError("""
                mutation {
                    addMagazineIssue(
                        magInput: {
                            pubDate: "2025-01-01"
                            index: { id: 999999999 }
                            info: {
                                issueName: "%s"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(issueName), "Unknown publication index ID: 999999999");
        assertThat(countPubsByIssueName(issueName)).isZero();
    }

    @Test
    void addMagazineIssueRejectsUnknownPublicationId() {
        final String serialNumber = "NEG-MAG-UNKNOWN-PUB-ID";
        final String issueName = "Negative Test Magazine Unknown Publication ID";

        assertValidationError("""
                mutation {
                    addMagazineIssue(
                        magInput: {
                            pubId: 999999999
                            pubDate: "2025-01-01"
                            index: {
                                data: {
                                    name: "Negative Test Magazine Unknown Publication ID"
                                    type: MAG
                                    serial: "%s"
                                }
                            }
                            info: {
                                issueName: "%s"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(serialNumber, issueName), "Unknown publication ID: 999999999");
        assertThat(countPubIndicesBySerialNumber(serialNumber)).isZero();
        assertThat(countPubsByIssueName(issueName)).isZero();
    }

    @Test
    void addMagazineIssueRejectsBookPublicationId() {
        final long bookPubId = pubIdForTomPettyGreatestHitsFirstPrinting();
        final long magazineIndexId = pubIndexIdBySerialNumber("10456295");
        final String issueName = "Negative Test Magazine With Book Publication ID";

        assertValidationError("""
                mutation {
                    addMagazineIssue(
                        magInput: {
                            pubId: %d
                            pubDate: "2025-01-01"
                            index: { id: %d }
                            info: {
                                issueName: "%s"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(bookPubId, magazineIndexId, issueName),
                "Pub type mismatch: input is for pub type MAG, but publication specified is for pub type BOOK.");
        assertThat(countPubsByIssueName(issueName)).isZero();
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
    void addBookEditionRejectsUnknownPublicationIndexReference() {
        final String edition = "Negative Test Book Unknown Index";

        assertValidationError("""
                mutation {
                    addBookEdition(
                        bookInput: {
                            pubDate: "2025-01-01"
                            index: { id: 999999999 }
                            info: {
                                edition: "%s"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(edition), "Unknown publication index ID: 999999999");
        assertThat(countPubsByEdition(edition)).isZero();
    }

    @Test
    void addBookEditionRejectsUnknownPublicationId() {
        final String serialNumber = "NEG-BOOK-UNKNOWN-PUB-ID";
        final String edition = "Negative Test Book Unknown Publication ID";

        assertValidationError("""
                mutation {
                    addBookEdition(
                        bookInput: {
                            pubId: 999999999
                            pubDate: "2025-01-01"
                            index: {
                                data: {
                                    name: "Negative Test Book Unknown Publication ID"
                                    type: BOOK
                                    serial: "%s"
                                }
                            }
                            info: {
                                edition: "%s"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(serialNumber, edition), "Unknown publication ID: 999999999");
        assertThat(countPubIndicesBySerialNumber(serialNumber)).isZero();
        assertThat(countPubsByEdition(edition)).isZero();
    }

    @Test
    void addBookEditionRejectsMagazinePublicationId() {
        final long magazinePubId = pubIdForGuitarWorldNovember2018();
        final long bookIndexId = pubIndexIdBySerialNumber("0898987660");
        final String edition = "Negative Test Book With Magazine Publication ID";

        assertValidationError("""
                mutation {
                    addBookEdition(
                        bookInput: {
                            pubId: %d
                            pubDate: "2025-01-01"
                            index: { id: %d }
                            info: {
                                edition: "%s"
                            }
                        }
                    ) {
                        id
                    }
                }
                """.formatted(magazinePubId, bookIndexId, edition),
                "Pub type mismatch: input is for pub type BOOK, but publication specified is for pub type MAG.");
        assertThat(countPubsByEdition(edition)).isZero();
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

    @Test
    void addTranscriptionRejectsUnknownPublicationReferenceAndRollsBackSong() {
        final String songTitle = "Negative Test Unknown Publication Song";

        assertValidationError("""
                mutation {
                    addTranscription(
                        pubId: 999999999
                        transcriptionInput: {
                            song: {
                                data: {
                                    title: "%s"
                                }
                            }
                            pageNumber: 12
                        }
                    ) {
                        id
                    }
                }
                """.formatted(songTitle), "Unknown publication ID: 999999999");
        assertThat(countSongsByTitle(songTitle)).isZero();
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

    private void assertRequestError(final String document, final String message) {
        graphQlTester.document(document)
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors)
                        .anySatisfy(error -> assertThat(error.getMessage()).contains(message)));
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

    private static long pubIdForTomPettyGreatestHitsFirstPrinting() {
        return queryForLong(DATABASE, """
                select p.id
                from gmdb.pub p
                    inner join gmdb.pub_idx pi on p.pub_idx_id = pi.id
                where pi.serial_number = '0898987660'
                    and p.details->>'edition' = 'First Printing'
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

    private static int countSongsByTitle(final String title) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.song
                where title = '%s'
                """.formatted(title));
    }

    private static int countArtistsByName(final String name) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.artist
                where name = '%s'
                """.formatted(name));
    }

    private static int countTranscribersByName(final String name) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcriber
                where name = '%s'
                """.formatted(name));
    }

    private static int countAlbumsByTitle(final String title) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.album
                where title = '%s'
                """.formatted(title));
    }

    private static int countTranscriptionsByPubIdAndPageNumber(final long pubId, final int pageNumber) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription
                where pub_id = %d
                    and details->>'pageNumber' = '%d'
                """.formatted(pubId, pageNumber));
    }

    private static int countPubsByIssueName(final String issueName) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where details->>'issueName' = '%s'
                """.formatted(issueName));
    }

    private static int countPubsByEdition(final String edition) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where details->>'edition' = '%s'
                """.formatted(edition));
    }

    private static int countPubIndicesBySerialNumber(final String serialNumber) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub_idx
                where serial_number = '%s'
                """.formatted(serialNumber));
    }
}
