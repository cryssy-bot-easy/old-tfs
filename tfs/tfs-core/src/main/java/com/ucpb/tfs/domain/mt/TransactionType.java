package com.ucpb.tfs.domain.mt;

public enum TransactionType {

    RTGS("RTGS"), FOREIGN("Foreign");

    private final String displayText;

    private TransactionType(final String displayText) {
        this.displayText = displayText;
    }

    public String AccountType() {
        return displayText;
    }

}
