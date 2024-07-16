package com.ucpb.tfs.domain.product.enums;

public enum AccountType {

    FCDU("FCDU"), RBU("RBU");

    private final String displayText;

    private AccountType(final String displayText) {
        this.displayText = displayText;
    }

    public String AccountType() {
        return displayText;
    }
}
