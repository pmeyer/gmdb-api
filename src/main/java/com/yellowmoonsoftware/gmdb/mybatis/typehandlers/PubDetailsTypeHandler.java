package com.yellowmoonsoftware.gmdb.mybatis.typehandlers;

import com.yellowmoonsoftware.gmdb.dto.output.PubDetails;
import com.yellowmoonsoftware.gmdb.mybatis.typehandlers.base.BaseJsonbTypeHandler;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(PubDetails.class)
public class PubDetailsTypeHandler extends BaseJsonbTypeHandler<PubDetails> {

    public PubDetailsTypeHandler() {
        super(PubDetails.class);
    }
}
