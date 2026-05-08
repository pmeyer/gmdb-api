package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SongInputTest {

    @Test
    void exposesRecordValuesAndDataContract() {
        SongInput.SongData data = songData();

        SongInput input = new SongInput(1L, data);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.data()).isSameAs(data);
        assertThat((IdAndDataContainer<SongInput.SongData>) input).extracting(IdAndDataContainer::data)
            .isSameAs(data);
    }

    @Test
    void songDataExposesValues() {
        SongArtistInput artist = new SongArtistInput(10L, null, Set.of(SongArtistRole.PERFORMED_BY));
        SongInput.AlbumTrackInput albumTrack = albumTrack();

        SongInput.SongData data = new SongInput.SongData("Opener", List.of(artist), albumTrack);

        assertThat(data.title()).isEqualTo("Opener");
        assertThat(data.artists()).containsExactly(artist);
        assertThat(data.albumTrack()).isSameAs(albumTrack);
    }

    @Test
    void albumTrackInputExposesValues() {
        AlbumInput album = albumInput();

        SongInput.AlbumTrackInput albumTrack = new SongInput.AlbumTrackInput(3, album);

        assertThat(albumTrack.trackNumber()).isEqualTo(3);
        assertThat(albumTrack.album()).isSameAs(album);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        SongInput.SongData data = songData();
        SongInput input = new SongInput(1L, data);
        SongInput sameValues = new SongInput(1L, data);
        SongInput differentId = new SongInput(2L, data);

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentId);
        assertThat(input.toString()).contains("id=1", "Opener");
    }

    private static SongInput.SongData songData() {
        return new SongInput.SongData(
            "Opener",
            List.of(new SongArtistInput(10L, null, Set.of(SongArtistRole.PERFORMED_BY))),
            albumTrack()
        );
    }

    private static SongInput.AlbumTrackInput albumTrack() {
        return new SongInput.AlbumTrackInput(3, albumInput());
    }

    private static AlbumInput albumInput() {
        ArtistInput artist = new ArtistInput(20L, new ArtistData("Alice", ArtistType.PERSON));
        AlbumData albumData = new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), artist);
        return new AlbumInput(30L, albumData);
    }
}
