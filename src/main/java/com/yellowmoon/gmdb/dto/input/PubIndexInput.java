package com.yellowmoon.gmdb.dto.input;

import com.yellowmoon.gmdb.dto.PubType;
import org.springframework.lang.NonNull;

public record PubIndexInput(
        Long id,
        @ConditionalNotNull(field = "id")
        String name,
        @ConditionalNotNull(field = "id")
        PubType type,
        @ConditionalNotNull(field = "id")
        String serial,
        @NonNull PubIndexInputFlag flag) { }
