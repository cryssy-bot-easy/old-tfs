package com.ucpb.tfs.domain.reference;

import java.io.Serializable;

public class ChargeDefaultsReference {

    private long id;
    private String matcher;
    private String type;
    private String value;


    public ChargeDefaultsReference() {
    }

    public ChargeDefaultsReference(String matcher, String type, String value) {

        this.matcher = matcher;
        this.type = type;
        this.value = value;
    }


    @Override
    public String toString() {
        return this.matcher;
    }

    public long getId() {
        return id;
    }

    public String getMatcher() {
        return matcher;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}