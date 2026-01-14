package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Artist;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base.BaseJsonbTypeHandler;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Artist.class)
public class JsonArtistTypeHandler extends BaseJsonbTypeHandler<Artist> {
    public JsonArtistTypeHandler() {
        super(Artist.class);
    }
}
