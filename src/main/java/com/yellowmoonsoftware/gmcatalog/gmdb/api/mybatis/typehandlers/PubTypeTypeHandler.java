package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base.PGCustomEnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(PubType.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class PubTypeTypeHandler extends PGCustomEnumTypeHandler<PubType> {
    public PubTypeTypeHandler() {
        super("pub_type", PubType.class);
    }
}
