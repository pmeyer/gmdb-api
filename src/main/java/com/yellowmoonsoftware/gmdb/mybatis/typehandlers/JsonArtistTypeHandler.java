package com.yellowmoonsoftware.gmdb.mybatis.typehandlers;

import com.yellowmoonsoftware.gmdb.dto.output.Artist;
import com.yellowmoonsoftware.gmdb.mybatis.typehandlers.base.BaseJsonbTypeHandler;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Artist.class)
public class JsonArtistTypeHandler extends BaseJsonbTypeHandler<Artist> {
    public JsonArtistTypeHandler() {
        super(Artist.class);
    }
}
