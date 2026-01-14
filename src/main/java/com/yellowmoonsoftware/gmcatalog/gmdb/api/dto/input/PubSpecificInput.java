package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;

public interface PubSpecificInput<T extends PubDetails> {
    T toDetails();
}
