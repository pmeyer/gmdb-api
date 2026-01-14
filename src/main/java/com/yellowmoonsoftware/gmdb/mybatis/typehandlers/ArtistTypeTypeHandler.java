package com.yellowmoonsoftware.gmdb.mybatis.typehandlers;

import com.yellowmoonsoftware.gmdb.dto.ArtistType;
import com.yellowmoonsoftware.gmdb.mybatis.typehandlers.base.PGCustomEnumTypeHandler;
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
