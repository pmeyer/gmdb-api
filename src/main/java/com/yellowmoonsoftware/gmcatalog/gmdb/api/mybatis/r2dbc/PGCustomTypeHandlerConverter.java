package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.r2dbc;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base.PGCustomTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.stereotype.Component;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.type.R2dbcTypeHandlerAdapter;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.type.converter.MybatisTypeHandlerConverter;

@Slf4j
@Component
public class PGCustomTypeHandlerConverter implements MybatisTypeHandlerConverter {
    @Override
    public boolean shouldConvert(TypeHandler<?> originalMybatisTypeHandler) {
        return originalMybatisTypeHandler instanceof PGCustomTypeHandler<?, ?>;
    }

    @Override
    public R2dbcTypeHandlerAdapter<?> convert(TypeHandler<?> originalMybatisTypeHandler) {
        return convert((PGCustomTypeHandler<?,?>) originalMybatisTypeHandler);
    }

    public static <T,R> PGCustomR2dbcTypeHandlerAdapter<T,R> convert(final PGCustomTypeHandler<T, R> handler) {
        log.debug("Converting {} to PGCustomR2dbcTypeHandlerAdapter", handler.getClass().getSimpleName());
        return new PGCustomR2dbcTypeHandlerAdapter<>(
                handler.getPgType(),
                handler,
                handler,
                handler.getType(),
                handler.getResultTypeClass());
    }
}
