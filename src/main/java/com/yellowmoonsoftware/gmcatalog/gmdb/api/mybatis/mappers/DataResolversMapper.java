package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;

import java.util.Set;

@Mapper
public interface DataResolversMapper {
    Flux<AlbumSearchResult> getAlbumsFromIds(@Param("albumIds") final Set<Long> albumIds);

    Flux<SongArtist> getSongArtistBySongIds(@Param("songIds") final Set<Long> songIds);

    Flux<Transcription> getSongTranscriptionBySongIds(@Param("songIds") final Set<Long> songIds);

    Flux<Transcription> getSongTranscriptionsByPubIds(@Param("pubIds") final Set<Long> pubIds);

    Flux<TranscriptionTranscriber> getTranscribersByTranscriptionIds(@Param("transcriptionIds") final Set<Long> transcriptionIds);

    Flux<TranscriptionPublication> getPublicationByTranscriptionIds(@Param("transcriptionIds") final Set<Long> transcriptionIds);

    Flux<SongSearchResult> getSongsBySongIds(@Param("songIds") final Set<Long> songIds);
}
