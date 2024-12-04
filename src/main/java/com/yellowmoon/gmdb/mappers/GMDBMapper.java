package com.yellowmoon.gmdb.mappers;

import com.yellowmoon.gmdb.dto.input.AlbumSearchCriteria;
import com.yellowmoon.gmdb.dto.input.ArtistSearchCriteria;
import com.yellowmoon.gmdb.dto.input.PubSearchCriteria;
import com.yellowmoon.gmdb.dto.input.SongSearchCriteria;
import com.yellowmoon.gmdb.dto.output.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GMDBMapper {
    List<Transcriber> getTranscribers(final String searchName);

    List<SongSearchResult> songSearch(@Param("criteria") final SongSearchCriteria criteria);
    List<ArtistSearchResult> artistSearch(@Param("criteria") final ArtistSearchCriteria criteria);
    List<PubSearchResult> pubSearch(@Param("criteria") final PubSearchCriteria criteria);
    List<AlbumSearchResult> albumSearch(@Param("criteria") final AlbumSearchCriteria criteria);
}
