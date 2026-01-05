package com.yellowmoonsoftware.gmdb.mappers.typehandler;

import com.yellowmoonsoftware.gmdb.dto.PubType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@Slf4j
@MappedTypes(PubType.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class PubTypeTypeHandler extends PGCustomTypeHandler<PubType, String> {
    public PubTypeTypeHandler() {
        super("pub_type", Enum::name, String.class, s -> Enum.valueOf(PubType.class, s));
    }
}
