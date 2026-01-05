package com.yellowmoonsoftware.gmdb.dto.input;

import com.yellowmoonsoftware.gmdb.dto.output.PubDetails;

public interface PubSpecificInput<T extends PubDetails> {
    T toDetails();
}
