package com.yellowmoonsoftware.gmdb.mappers;

import com.yellowmoonsoftware.gmdb.dto.PubType;
import com.yellowmoonsoftware.gmdb.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmdb.dto.output.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Mapper
public interface DataResolversMapper {
    @MapKey("id")
    Map<Long, AlbumSearchResult> getAlbumsFromIds(@Param("albumIds") final Set<Long> albumIds);

    Set<SongArtist> getSongArtistBySongIds(@Param("songIds") final Set<Long> songIds);

    Set<Transcription> getSongTranscriptionBySongIds(@Param("songIds") final Set<Long> songIds);

    Set<Transcription> getSongTranscriptionsByPubIds(@Param("pubIds") final Set<Long> pubIds);

    Set<TranscriptionTranscriber> getTranscribersByTranscriptionIds(@Param("transcriptionIds") final Set<Long> transcriptionIds);

    @MapKey("transcriptionId")
    Map<Long, TranscriptionPublication> getPublicationByTranscriptionIds(@Param("transcriptionIds") final Set<Long> transcriptionIds);

    Set<SongSearchResult> getSongsBySongIds(@Param("songIds") final Set<Long> songIds);
}
