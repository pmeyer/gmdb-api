package com.yellowmoonsoftware.gmdb.mappers;

import com.yellowmoonsoftware.gmdb.dto.input.AlbumSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.ArtistSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.PubSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.input.SongSearchCriteria;
import com.yellowmoonsoftware.gmdb.dto.output.*;
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
