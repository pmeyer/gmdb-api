package com.yellowmoonsoftware.gmdb.mappers.typehandler;

import com.yellowmoonsoftware.gmdb.dto.output.Artist;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Artist.class)
public class JsonArtistTypeHandler extends BaseJsonbTypeHandler<Artist> {
    public JsonArtistTypeHandler() {
        super(Artist.class);
    }
}
