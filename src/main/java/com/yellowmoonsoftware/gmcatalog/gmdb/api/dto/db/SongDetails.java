package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type.JsonTypeHandler;

@JsonTypeHandler
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SongDetails(Integer trackNumber) { }
