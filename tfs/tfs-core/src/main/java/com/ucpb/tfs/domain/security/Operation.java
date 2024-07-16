package com.ucpb.tfs.domain.security;

public enum Operation {

    CREATE("C"), VIEW("V"), UPDATE("U"), EXECUTE("X");

    private final String displayText;

    private Operation(final String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

}
