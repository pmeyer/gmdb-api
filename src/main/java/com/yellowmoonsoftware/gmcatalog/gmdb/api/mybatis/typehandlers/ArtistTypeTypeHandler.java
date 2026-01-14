package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base.PGCustomEnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(ArtistType.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class ArtistTypeTypeHandler extends PGCustomEnumTypeHandler<ArtistType> {
    public ArtistTypeTypeHandler() {
        super("artist_type", ArtistType.class);
    }
}
