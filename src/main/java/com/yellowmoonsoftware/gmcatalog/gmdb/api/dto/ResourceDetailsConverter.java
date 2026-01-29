package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

public interface ResourceDetailsConverter<T extends ResourceBundle> {
    T toDetails();
}
