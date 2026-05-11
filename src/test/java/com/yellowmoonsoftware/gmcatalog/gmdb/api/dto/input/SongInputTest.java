package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class SongInputTest {

    @Test
    void exposesRecordValuesAndDataContract() {
        final SongInput.SongData data = songData();

        final SongInput input = new SongInput(1L, data);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.data()).isSameAs(data);
        assertThat((IdAndDataContainer<SongInput.SongData>) input).extracting(IdAndDataContainer::data)
            .isSameAs(data);
    }

    @Test
    void songDataExposesValues() {
        final SongArtistInput artist = new SongArtistInput(10L, null, Set.of(SongArtistRole.PERFORMED_BY));
        final SongInput.AlbumTrackInput albumTrack = albumTrack();

        final SongInput.SongData data = new SongInput.SongData("Opener", List.of(artist), albumTrack);

        assertThat(data.title()).isEqualTo("Opener");
        assertThat(data.artists()).containsExactly(artist);
        assertThat(data.albumTrack()).isSameAs(albumTrack);
    }

    @Test
    void albumTrackInputExposesValues() {
        final AlbumInput album = albumInput();

        final SongInput.AlbumTrackInput albumTrack = new SongInput.AlbumTrackInput(3, album);

        assertThat(albumTrack.trackNumber()).isEqualTo(3);
        assertThat(albumTrack.album()).isSameAs(album);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final SongInput.SongData data = songData();
        final SongInput input = new SongInput(1L, data);
        final SongInput sameValues = new SongInput(1L, data);
        final SongInput differentId = new SongInput(2L, data);

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentId);
        assertThat(input.toString()).contains("id=1", "Opener");
    }

    @Test
    void validatesWhenIdOrDataIsPresent() {
        final SongInput idOnly = new SongInput(1L, null);
        final SongInput dataOnly = new SongInput(null, songData());

        assertThat(ValidationTestSupport.validate(idOnly)).isEmpty();
        assertThat(ValidationTestSupport.validate(dataOnly)).isEmpty();
    }

    @Test
    void validatesIdOrDataRequirement() {
        final SongInput input = new SongInput(null, null);

        assertThat(ValidationTestSupport.validate(input))
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("SongInput must have an ID or data");
    }

    @Test
    void cascadesValidationToSongArtists() {
        final SongInput.SongData data = new SongInput.SongData(
            "Opener",
            List.of(new SongArtistInput(null, null, Set.of())),
            null
        );
        final SongInput input = new SongInput(null, data);

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("data.artists[0]", "SongArtistInput must have an ID or data"));
    }

    @Test
    void cascadesValidationToAlbumTrackAlbum() {
        final SongInput.AlbumTrackInput invalidTrack = new SongInput.AlbumTrackInput(3, new AlbumInput(null, null));
        final SongInput.SongData data = new SongInput.SongData("Opener", List.of(), invalidTrack);
        final SongInput input = new SongInput(null, data);

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("data.albumTrack.album", "AlbumInput must have an ID or data"));
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
        final ArtistInput artist = new ArtistInput(20L, new ArtistData("Alice", ArtistType.PERSON));
        final AlbumData albumData = new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), artist);
        return new AlbumInput(30L, albumData);
    }
}
