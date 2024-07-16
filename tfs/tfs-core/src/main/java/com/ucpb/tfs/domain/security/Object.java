package com.ucpb.tfs.domain.security;

import java.io.Serializable;

public class Object implements Serializable {

    String code;
    String description;

    public Object() {}

    public Object(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return code;
    }


}
