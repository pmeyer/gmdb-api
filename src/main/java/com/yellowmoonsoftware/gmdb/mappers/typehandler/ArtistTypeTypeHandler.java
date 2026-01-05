package com.yellowmoonsoftware.gmdb.mappers.typehandler;

import com.yellowmoonsoftware.gmdb.dto.ArtistType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@Slf4j
@MappedTypes(ArtistType.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class ArtistTypeTypeHandler extends PGCustomTypeHandler<ArtistType, String> {
    public ArtistTypeTypeHandler() {
        super("artist_type", Enum::name, String.class, s -> Enum.valueOf(ArtistType.class, s));
    }
}
