package com.ucpb.tfs.domain.product.enums;

/**
 * User: IPCVal
 * Date: 11/23/12
 */
public enum IndemnityCodeEnum {

    BG_ISSUANCE("21"),
    BE_ISSUANCE("22");

    private String documentCode;

    private IndemnityCodeEnum(String documentCode) {
        this.documentCode = documentCode;
    }

    @Override
    public String toString() {
        return this.documentCode;
    }
}
