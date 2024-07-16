package com.ucpb.tfs.domain.reference;

/**
 * User: giancarlo
 * Date: 10/10/12
 * Time: 1:19 PM
 */
public class ValueHolder {

    private String id;
    private String token;
    private String value;

    public ValueHolder() {
    }

    public ValueHolder(String token, String value) {
        this.token = token;
        this.value = value;
    }

    public String getToken() {
        return token;
    }

    public String getValue() {
        return value;
    }
}
