package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base.BaseJsonbTypeHandler;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(PubDetails.class)
public class PubDetailsTypeHandler extends BaseJsonbTypeHandler<PubDetails> {

    public PubDetailsTypeHandler() {
        super(PubDetails.class);
    }
}
