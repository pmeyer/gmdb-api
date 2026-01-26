package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.http.MediaType;

public record ResourceAttributes(
        String originalFilename,
        @JsonSerialize(using = ToStringSerializer.class)
        MediaType mediaType) {

}
