package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class GmdbBaselineDataIntegrationTests extends GmdbDatabaseIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedDatabase();

    @BeforeAll
    static void applyTestData() {
        applyBaselineTestData(DATABASE);
    }

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerGmdbIntegrationProperties(registry, DATABASE);
    }

    @Test
    void appliesBaselineTestDataSql() {
        assertThat(queryForInt(DATABASE, """
                select count(*)
                from gmdb.artist
                where name = 'AC/DC'
                    and type::text = 'BAND'
                """)).isEqualTo(1);

        assertThat(queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription tran
                    inner join gmdb.song song on tran.song_id = song.id
                    inner join gmdb.album album on song.album_id = album.id
                    inner join gmdb.pub pub on tran.pub_id = pub.id
                    inner join gmdb.pub_idx pub_idx on pub.pub_idx_id = pub_idx.id
                    inner join gmdb.transcription_transcriber tran_transcriber
                        on tran.id = tran_transcriber.transcription_id
                    inner join gmdb.transcriber transcriber
                        on tran_transcriber.transcriber_id = transcriber.id
                where song.title = 'Rocket Queen'
                    and album.title = 'Appetite For Destruction'
                    and pub_idx.name = 'Guitar World'
                    and transcriber.name = 'Andy Aledort'
                """)).isEqualTo(1);
    }

    @Test
    void generatedResourceIdsAreAddedToResourceDetails() {
        assertGeneratedResourceIds("album");
        assertGeneratedResourceIds("pub");
        assertGeneratedResourceIds("song");
        assertGeneratedResourceIds("transcription");

        assertThat(queryForInt(DATABASE, """
                select count(*)
                from gmdb.album
                where title = 'Appetite For Destruction'
                    and details->>'resourceId' = '4b8c2c6f-0a74-4d5a-a271-f4734b6ce8a2'
                    and details->>'releaseDate' = '1987-07-21'
                    and details->'resources'->'ALBUM_ART'->>'originalFilename' = 'gmdb-test-album-art.png'
                    and details->'resources'->'ALBUM_ART'->>'mediaType' = 'image/png'
                """)).isEqualTo(1);

        assertThat(queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub pub
                    inner join gmdb.pub_idx pub_idx on pub.pub_idx_id = pub_idx.id
                where pub_idx.name = 'Guitar World'
                    and pub.details->>'resourceId' = '9d4c6f61-2c7d-49d1-9e36-0e99afef0cf7'
                    and pub.details->>'volume' = '39'
                    and pub.details->>'issue' = '11'
                    and pub.details->>'issueName' = 'November 2018'
                    and pub.details->'resources'->'COVER_IMAGE'->>'originalFilename' = 'gmdb-test-cover-image.png'
                    and pub.details->'resources'->'COVER_IMAGE'->>'mediaType' = 'image/png'
                """)).isEqualTo(1);

        assertThat(queryForInt(DATABASE, """
                select count(*)
                from gmdb.song
                where title = 'American Girl'
                    and details ? 'resourceId'
                    and details->>'trackNumber' = '1'
                """)).isEqualTo(1);

        assertThat(queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription tran
                    inner join gmdb.song song on tran.song_id = song.id
                    inner join gmdb.album album on song.album_id = album.id
                where song.title = 'Rocket Queen'
                    and album.title = 'Appetite For Destruction'
                    and tran.details->>'resourceId' = 'f8e2c95a-6f45-44cb-8a4f-2e7e33f3df70'
                    and tran.details->>'pageNumber' = '98'
                    and tran.details->'resources'->'TRANSCRIPTION'->>'originalFilename' = 'gmdb-test-transcription.pdf'
                    and tran.details->'resources'->'TRANSCRIPTION'->>'mediaType' = 'application/pdf'
                """)).isEqualTo(1);
    }

    private static void assertGeneratedResourceIds(final String tableName) {
        assertThat(queryForInt(DATABASE, """
                select count(*)
                from gmdb.%s
                where details->>'resourceId' is null
                """.formatted(tableName))).isZero();
    }
}
