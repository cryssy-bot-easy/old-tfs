package com.ucpb.tfs.domain.product.enums;

public enum DCDocumentType {
	
	FOREIGN("FX"), DOMESTIC("DM");

    private final String code;

    DCDocumentType(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
