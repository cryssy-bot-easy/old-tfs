package com.ucpb.tfs.domain.product.enums;

/**
 * User: Jett
 * Date: 7/16/12
 */
public enum LCDocumentType {

    FOREIGN("FX"), DOMESTIC("DM");

    private final String code;

    LCDocumentType(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
