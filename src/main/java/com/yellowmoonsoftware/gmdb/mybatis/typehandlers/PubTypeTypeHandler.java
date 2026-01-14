package com.yellowmoonsoftware.gmdb.mybatis.typehandlers;

import com.yellowmoonsoftware.gmdb.dto.PubType;
import com.yellowmoonsoftware.gmdb.mybatis.typehandlers.base.PGCustomEnumTypeHandler;
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
