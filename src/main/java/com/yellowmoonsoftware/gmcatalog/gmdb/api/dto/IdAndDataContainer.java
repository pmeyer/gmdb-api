package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

public interface IdAndDataContainer<T> {
    Long id();
    T data();

    default DataMode mode() {
        if (id() == null){
            return DataMode.ADD;
        }
        if (data() == null) {
            return DataMode.REF;
        }
        return DataMode.UPDATE;
    }

    enum DataMode {
        REF,
        ADD,
        UPDATE
    }
}
