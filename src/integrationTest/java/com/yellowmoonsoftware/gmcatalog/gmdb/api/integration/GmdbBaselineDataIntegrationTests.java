package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GmdbBaselineDataIntegrationTests extends GmdbDatabaseIntegrationTestSupport {

    @BeforeAll
    static void applyTestData() {
        applyBaselineTestData();
    }

    @Test
    void appliesBaselineTestDataSql() {
        assertThat(queryForInt("""
                select count(*)
                from gmdb.artist
                where name = 'AC/DC'
                    and type::text = 'BAND'
                """)).isEqualTo(1);

        assertThat(queryForInt("""
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

        assertThat(queryForInt("""
                select count(*)
                from gmdb.album
                where title = 'Appetite For Destruction'
                    and details ? 'resourceId'
                    and details - 'resourceId' = '{}'::jsonb
                """)).isEqualTo(1);

        assertThat(queryForInt("""
                select count(*)
                from gmdb.pub pub
                    inner join gmdb.pub_idx pub_idx on pub.pub_idx_id = pub_idx.id
                where pub_idx.name = 'Guitar World'
                    and pub.details ? 'resourceId'
                    and pub.details->>'volume' = '39'
                    and pub.details->>'issue' = '11'
                    and pub.details->>'issueName' = 'November 2018'
                """)).isEqualTo(1);

        assertThat(queryForInt("""
                select count(*)
                from gmdb.song
                where title = 'American Girl'
                    and details ? 'resourceId'
                    and details->>'trackNumber' = '1'
                """)).isEqualTo(1);

        assertThat(queryForInt("""
                select count(*)
                from gmdb.transcription tran
                    inner join gmdb.song song on tran.song_id = song.id
                    inner join gmdb.album album on song.album_id = album.id
                where song.title = 'Rocket Queen'
                    and album.title = 'Appetite For Destruction'
                    and tran.details ? 'resourceId'
                    and tran.details->>'pageNumber' = '98'
                """)).isEqualTo(1);
    }

    private static void assertGeneratedResourceIds(final String tableName) {
        assertThat(queryForInt("""
                select count(*)
                from gmdb.%s
                where details->>'resourceId' is null
                """.formatted(tableName))).isZero();
    }
}
