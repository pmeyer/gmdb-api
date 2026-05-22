package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AddTranscriptionMutationIntegrationTests extends GmdbGraphQlMutationIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedMutationDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerMutationIntegrationProperties(registry, DATABASE);
    }

    @Test
    void addTranscriptionWithExistingPublicationSongAndTranscriberCreatesTranscription() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long songId = songIdByTitleAndAlbum("American Girl", "Greatest Hits");
        final long transcriberId = transcriberIdByName("Andy Aledort");

        final var result = addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: { id: %d }
                    pageNumber: 44
                    transcribers: [{ id: %d }]
                }
                """.formatted(pubId, songId, transcriberId));

        assertThat(result.id()).isPositive();
        assertThat(result.pageNumber()).isEqualTo(44);
        assertThat(result.url()).isNull();
        assertThat(result.song()).isEqualTo(new SongResponse(songId, "American Girl"));
        assertThat(result.transcribers()).containsExactly(new TranscriberResponse(transcriberId, "Andy Aledort"));
        assertThat(countTranscriptions(songId, pubId, 44)).isOne();
        assertThat(countTranscriptionTranscribers(result.id(), transcriberId)).isOne();
    }

    @Test
    void addTranscriptionWithNewTranscriberCreatesTranscriberAndAssociation() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long songId = songIdByTitleAndAlbum("Breakdown", "Greatest Hits");

        final var result = addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: { id: %d }
                    pageNumber: 45
                    transcribers: [{ name: "Mutation Test Transcriber" }]
                }
                """.formatted(pubId, songId));

        final long transcriberId = transcriberIdByName("Mutation Test Transcriber");

        assertThat(result.id()).isPositive();
        assertThat(result.pageNumber()).isEqualTo(45);
        assertThat(result.song()).isEqualTo(new SongResponse(songId, "Breakdown"));
        assertThat(result.transcribers()).containsExactly(new TranscriberResponse(
                transcriberId,
                "Mutation Test Transcriber"));
        assertThat(countTranscribersByName("Mutation Test Transcriber")).isOne();
        assertThat(countTranscriptions(songId, pubId, 45)).isOne();
        assertThat(countTranscriptionTranscribers(result.id(), transcriberId)).isOne();
    }

    @Test
    void addTranscriptionWithExistingSongAndPublicationUpdatesExistingTranscription() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long songId = songIdByTitleAndAlbum("Rocket Queen", "Appetite For Destruction");
        final long existingTranscriptionId = transcriptionId(songId, pubId);
        final long replacementTranscriberId = transcriberIdByName("Dave Whitehill");

        final var result = addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: { id: %d }
                    pageNumber: 97
                    transcribers: [{ id: %d }]
                }
                """.formatted(pubId, songId, replacementTranscriberId));

        assertThat(result.id()).isEqualTo(existingTranscriptionId);
        assertThat(result.pageNumber()).isEqualTo(97);
        assertThat(result.song()).isEqualTo(new SongResponse(songId, "Rocket Queen"));
        assertThat(result.transcribers()).containsExactly(new TranscriberResponse(
                replacementTranscriberId,
                "Dave Whitehill"));
        assertThat(countTranscriptions(songId, pubId, 98)).isZero();
        assertThat(countTranscriptions(songId, pubId, 97)).isOne();
        assertThat(countTranscriptionTranscribers(result.id())).isOne();
        assertThat(countTranscriptionTranscribers(result.id(), replacementTranscriberId)).isOne();
    }

    @Test
    void addTranscriptionWithNewSongAndExistingAlbumAndArtistReferencesCreatesRelatedData() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long albumId = albumIdByTitle("Appetite For Destruction");
        final long artistId = artistIdByNameAndType("Guns N' Roses", "BAND");
        final long transcriberId = transcriberIdByName("Andy Aledort");

        final var result = addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: {
                        data: {
                            title: "Mutation Test Song"
                            artists: [{ id: %d, roles: [PERFORMED_BY] }]
                            albumTrack: {
                                trackNumber: 99
                                album: { id: %d }
                            }
                        }
                    }
                    pageNumber: 55
                    transcribers: [{ id: %d }]
                }
                """.formatted(pubId, artistId, albumId, transcriberId));

        final long songId = songIdByTitleAndAlbum("Mutation Test Song", "Appetite For Destruction");

        assertThat(result.id()).isPositive();
        assertThat(result.pageNumber()).isEqualTo(55);
        assertThat(result.song()).isEqualTo(new SongResponse(songId, "Mutation Test Song"));
        assertThat(result.transcribers()).containsExactly(new TranscriberResponse(transcriberId, "Andy Aledort"));
        assertThat(countSongsByTitleAlbumAndTrackNumber("Mutation Test Song", albumId, 99)).isOne();
        assertThat(countSongArtistsByRole(songId, artistId, "PERFORMED_BY")).isOne();
        assertThat(countTranscriptions(songId, pubId, 55)).isOne();
        assertThat(countTranscriptionTranscribers(result.id(), transcriberId)).isOne();
    }

    @Test
    void addTranscriptionWithNaturalArtistAndTranscriberDataReusesExistingRows() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long albumId = albumIdByTitle("Appetite For Destruction");
        final long artistId = artistIdByNameAndType("Guns N' Roses", "BAND");
        final long transcriberId = transcriberIdByName("Andy Aledort");

        final var result = addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: {
                        data: {
                            title: "Mutation Test Natural Reuse Song"
                            artists: [{
                                data: {
                                    name: "Guns N' Roses"
                                    type: BAND
                                }
                                roles: [PERFORMED_BY]
                            }]
                            albumTrack: {
                                trackNumber: 100
                                album: { id: %d }
                            }
                        }
                    }
                    pageNumber: 56
                    transcribers: [{ name: "Andy Aledort" }]
                }
                """.formatted(pubId, albumId));

        final long songId = songIdByTitleAndAlbum(
                "Mutation Test Natural Reuse Song",
                "Appetite For Destruction");

        assertThat(result.id()).isPositive();
        assertThat(result.pageNumber()).isEqualTo(56);
        assertThat(result.song()).isEqualTo(new SongResponse(songId, "Mutation Test Natural Reuse Song"));
        assertThat(result.transcribers()).containsExactly(new TranscriberResponse(transcriberId, "Andy Aledort"));
        assertThat(countArtistsByNameAndType("Guns N' Roses", "BAND")).isOne();
        assertThat(countTranscribersByName("Andy Aledort")).isOne();
        assertThat(countSongsByTitleAlbumAndTrackNumber(
                "Mutation Test Natural Reuse Song",
                albumId,
                100)).isOne();
        assertThat(countSongArtistsByRole(songId, artistId, "PERFORMED_BY")).isOne();
        assertThat(countTranscriptions(songId, pubId, 56)).isOne();
        assertThat(countTranscriptionTranscribers(result.id(), transcriberId)).isOne();
    }

    @Test
    void addTranscriptionWithNewSongAlbumAndPrimaryArtistDataCreatesRelatedData() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long transcriberId = transcriberIdByName("Jeff Perrin");

        final var result = addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: {
                        data: {
                            title: "Mutation Test Album Song"
                            artists: []
                            albumTrack: {
                                trackNumber: 1
                                album: {
                                    data: {
                                        title: "Mutation Test Album"
                                        releaseDate: "2025-07-01"
                                        primaryArtist: {
                                            data: {
                                                name: "Mutation Test Album Artist"
                                                type: PERSON
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    pageNumber: 57
                    transcribers: [{ id: %d }]
                }
                """.formatted(pubId, transcriberId));

        final long albumId = albumIdByTitle("Mutation Test Album");
        final long songId = songIdByTitleAndAlbum("Mutation Test Album Song", "Mutation Test Album");
        final long artistId = artistIdByNameAndType("Mutation Test Album Artist", "PERSON");

        assertThat(result.id()).isPositive();
        assertThat(result.pageNumber()).isEqualTo(57);
        assertThat(result.song()).isEqualTo(new SongResponse(songId, "Mutation Test Album Song"));
        assertThat(result.transcribers()).containsExactly(new TranscriberResponse(transcriberId, "Jeff Perrin"));
        assertThat(countAlbumsByTitlePrimaryArtistAndReleaseDate(
                "Mutation Test Album",
                artistId,
                "2025-07-01")).isOne();
        assertThat(countSongsByTitleAlbumAndTrackNumber("Mutation Test Album Song", albumId, 1)).isOne();
        assertThat(countTranscriptions(songId, pubId, 57)).isOne();
        assertThat(countTranscriptionTranscribers(result.id(), transcriberId)).isOne();
    }

    @Test
    void addTranscriptionWithMatchingAlbumDataReusesAndUpdatesAlbum() {
        final long pubId = pubIdForGuitarWorldNovember2018();
        final long transcriberId = transcriberIdByName("Jeff Perrin");

        addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: {
                        data: {
                            title: "Mutation Test Album Upsert Song One"
                            artists: []
                            albumTrack: {
                                trackNumber: 1
                                album: {
                                    data: {
                                        title: "Mutation Test Album Upsert"
                                        releaseDate: "2025-08-01"
                                        primaryArtist: {
                                            data: {
                                                name: "Mutation Test Album Upsert Artist"
                                                type: PERSON
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    pageNumber: 58
                    transcribers: [{ id: %d }]
                }
                """.formatted(pubId, transcriberId));

        final long originalAlbumId = albumIdByTitle("Mutation Test Album Upsert");

        final var result = addTranscription("""
                pubId: %d
                transcriptionInput: {
                    song: {
                        data: {
                            title: "Mutation Test Album Upsert Song Two"
                            artists: []
                            albumTrack: {
                                trackNumber: 2
                                album: {
                                    data: {
                                        title: "Mutation Test Album Upsert"
                                        releaseDate: "2025-08-02"
                                        primaryArtist: {
                                            data: {
                                                name: "Mutation Test Album Upsert Artist"
                                                type: PERSON
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    pageNumber: 59
                    transcribers: [{ id: %d }]
                }
                """.formatted(pubId, transcriberId));

        final long artistId = artistIdByNameAndType("Mutation Test Album Upsert Artist", "PERSON");
        final long secondSongId = songIdByTitleAndAlbum("Mutation Test Album Upsert Song Two", "Mutation Test Album Upsert");

        assertThat(result.id()).isPositive();
        assertThat(result.song()).isEqualTo(new SongResponse(secondSongId, "Mutation Test Album Upsert Song Two"));
        assertThat(albumIdByTitle("Mutation Test Album Upsert")).isEqualTo(originalAlbumId);
        assertThat(countArtistsByNameAndType("Mutation Test Album Upsert Artist", "PERSON")).isOne();
        assertThat(countAlbumsByTitlePrimaryArtistAndReleaseDate(
                "Mutation Test Album Upsert",
                artistId,
                "2025-08-02")).isOne();
        assertThat(countSongsByTitleAlbumAndTrackNumber(
                "Mutation Test Album Upsert Song One",
                originalAlbumId,
                1)).isOne();
        assertThat(countSongsByTitleAlbumAndTrackNumber(
                "Mutation Test Album Upsert Song Two",
                originalAlbumId,
                2)).isOne();
        assertThat(countTranscriptions(secondSongId, pubId, 59)).isOne();
        assertThat(countTranscriptionTranscribers(result.id(), transcriberId)).isOne();
    }

    private TranscriptionResponse addTranscription(final String inputFields) {
        return graphQlTester.document("""
                        mutation {
                            addTranscription(%s) {
                                id
                                url
                                pageNumber
                                song {
                                    id
                                    title
                                }
                                transcribers {
                                    id
                                    name
                                }
                            }
                        }
                        """.formatted(inputFields))
                .execute()
                .path("addTranscription")
                .entity(TranscriptionResponse.class)
                .get();
    }

    private static long pubIdForGuitarWorldNovember2018() {
        return queryForLong(DATABASE, """
                select p.id
                from gmdb.pub p
                    inner join gmdb.pub_idx pi on p.pub_idx_id = pi.id
                where pi.serial_number = '10456295'
                    and p.details->>'issueName' = 'November 2018'
                """);
    }

    private static long songIdByTitleAndAlbum(final String title, final String albumTitle) {
        return queryForLong(DATABASE, """
                select s.id
                from gmdb.song s
                    inner join gmdb.album a on s.album_id = a.id
                where s.title = '%s'
                    and a.title = '%s'
                """.formatted(title, albumTitle));
    }

    private static long transcriberIdByName(final String name) {
        return queryForLong(DATABASE, """
                select id
                from gmdb.transcriber
                where name = '%s'
                """.formatted(name));
    }

    private static long artistIdByNameAndType(final String name, final String type) {
        return queryForLong(DATABASE, """
                select id
                from gmdb.artist
                where name = '%s'
                    and type = '%s'::artist_type
                """.formatted(name.replace("'", "''"), type));
    }

    private static int countArtistsByNameAndType(final String name, final String type) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.artist
                where name = '%s'
                    and type = '%s'::artist_type
                """.formatted(name.replace("'", "''"), type));
    }

    private static long albumIdByTitle(final String title) {
        return queryForLong(DATABASE, """
                select id
                from gmdb.album
                where title = '%s'
                """.formatted(title.replace("'", "''")));
    }

    private static int countAlbumsByTitlePrimaryArtistAndReleaseDate(
            final String title,
            final long primaryArtistId,
            final String releaseDate) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.album
                where title = '%s'
                    and primary_artist_id = %d
                    and details->>'releaseDate' = '%s'
                """.formatted(title.replace("'", "''"), primaryArtistId, releaseDate));
    }

    private static long transcriptionId(final long songId, final long pubId) {
        return queryForLong(DATABASE, """
                select id
                from gmdb.transcription
                where song_id = %d
                    and pub_id = %d
                """.formatted(songId, pubId));
    }

    private static int countTranscribersByName(final String name) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcriber
                where name = '%s'
                """.formatted(name.replace("'", "''")));
    }

    private static int countTranscriptions(
            final long songId,
            final long pubId,
            final int pageNumber) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription
                where song_id = %d
                    and pub_id = %d
                    and details->>'pageNumber' = '%d'
                """.formatted(songId, pubId, pageNumber));
    }

    private static int countSongsByTitleAlbumAndTrackNumber(
            final String title,
            final long albumId,
            final int trackNumber) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.song
                where title = '%s'
                    and album_id = %d
                    and details->>'trackNumber' = '%d'
                """.formatted(title.replace("'", "''"), albumId, trackNumber));
    }

    private static int countSongArtistsByRole(
            final long songId,
            final long artistId,
            final String role) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.song_artist
                where song_id = %d
                    and artist_id = %d
                    and roles ? '%s'
                """.formatted(songId, artistId, role));
    }

    private static int countTranscriptionTranscribers(final long transcriptionId) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription_transcriber
                where transcription_id = %d
                """.formatted(transcriptionId));
    }

    private static int countTranscriptionTranscribers(
            final long transcriptionId,
            final long transcriberId) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription_transcriber
                where transcription_id = %d
                    and transcriber_id = %d
                """.formatted(transcriptionId, transcriberId));
    }

    private record TranscriptionResponse(
            Long id,
            String url,
            Integer pageNumber,
            SongResponse song,
            List<TranscriberResponse> transcribers) {
    }

    private record SongResponse(Long id, String title) {
    }

    private record TranscriberResponse(Long id, String name) {
    }
}
