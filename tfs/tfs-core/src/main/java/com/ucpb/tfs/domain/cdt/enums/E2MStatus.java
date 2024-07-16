package com.ucpb.tfs.domain.cdt.enums;

public enum E2MStatus {

    NEW("NEW"), PENDING("PENDING"), REJECTED("REJECTED"),
    DORMANT("DORMANT"), SENTTOBOC("BOC"), ABANDONED("ABANDONED"), CONFIRMED("CONFIRMED");

    private final String code;

    E2MStatus(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
