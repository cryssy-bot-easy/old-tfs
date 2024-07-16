package com.ucpb.tfs.domain.cdt.enums;

public enum PaymentRequestType {

    FINAL("FINAL"),
    ADVANCE("ADVANCE"),
    IPF("IPF"),
    EXPORT("EXPORT"),
    IPF_EXPORT_CHARGES("IPF_EXPORT_CHARGES"),
    FINAL_ADVANCE_CDT("FINAL_ADVANCE_CDT"),
    DOCSTAMP_FEE("DOCSTAMP_FEE");

    private final String code;

    PaymentRequestType(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
