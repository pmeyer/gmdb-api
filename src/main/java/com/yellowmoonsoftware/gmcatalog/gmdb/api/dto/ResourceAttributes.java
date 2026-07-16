package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import org.springframework.http.MediaType;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record ResourceAttributes(
        String originalFilename,
        @JsonSerialize(using = ToStringSerializer.class)
        MediaType mediaType) {

}
