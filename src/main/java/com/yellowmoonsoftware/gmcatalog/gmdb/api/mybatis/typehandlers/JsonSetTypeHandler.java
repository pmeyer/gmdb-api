package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base.BaseJsonbTypeHandler;
import lombok.NonNull;
import org.apache.ibatis.type.MappedTypes;

import java.util.Set;

@MappedTypes(Set.class)
public class JsonSetTypeHandler<T extends Set<T>> extends BaseJsonbTypeHandler<T> {
    public JsonSetTypeHandler(@NonNull final Class<T> type) {
        super(type);
    }
}
