package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.util.Objects;

public record AlbumIn(Long id,
                      String title,
                      AlbumDetails details,
                      Long primaryArtistId) {
    static public AlbumIn from(final AlbumInput input, final Long primaryArtistId) {
        final Objects.Getter<AlbumData> data = Objects.safeGetter(input.data());

        return new AlbumIn(input.id(),
                data.get(AlbumData::title),
                data.get(AlbumData::toDetails),
                primaryArtistId);
    }
}
