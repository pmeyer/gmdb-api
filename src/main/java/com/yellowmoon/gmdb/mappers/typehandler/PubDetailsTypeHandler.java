package com.yellowmoon.gmdb.mappers.typehandler;

import com.yellowmoon.gmdb.dto.output.PubDetails;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(PubDetails.class)
public class PubDetailsTypeHandler extends BaseJsonbTypeHandler<PubDetails> {

    public PubDetailsTypeHandler() {
        super(PubDetails.class);
    }
}
