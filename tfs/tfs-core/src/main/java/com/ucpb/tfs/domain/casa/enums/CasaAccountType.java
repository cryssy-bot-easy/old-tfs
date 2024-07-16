package com.ucpb.tfs.domain.casa.enums;

/**
 * Created by Marv on 2/27/14.
 */
public enum CasaAccountType {

    D("CURRENT"), S("SAVINGS");

    private final String value;

    private CasaAccountType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
