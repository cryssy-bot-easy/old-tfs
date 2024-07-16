package com.ucpb.tfs.domain.product.enums;

public enum CreditFacilityCode {

    SHORTTERM("Short Term"), LONGTERM("Long Term");

    private final String code;

    CreditFacilityCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
