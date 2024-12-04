package com.yellowmoon.gmdb.dto.input;

import com.yellowmoon.gmdb.dto.output.PubDetails;

public interface PubSpecificInput<T extends PubDetails> {
    T toDetails();
}
