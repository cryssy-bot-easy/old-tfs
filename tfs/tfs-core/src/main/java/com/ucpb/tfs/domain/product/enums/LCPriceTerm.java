package com.ucpb.tfs.domain.product.enums;

public enum LCPriceTerm {
    EXW("Ex-Works"),        // Ex-Works
    FCA("Free Carrier"),        // Free Carrier
    FAS("Free Alongside Ship"),        // Free Alongside Ship
    FOB("Free on Board"),        // Free on Board
    CFR("Cost and Freight"),        // Cost and Freight
    CIF("Cost, Insurance and Freight"),        // Cost, Insurance and Freight
    CPT("Carriage Paid To"),        // Carriage Paid To
    CIP("Carriage and Insurance Paid To"),        // Carriage and Insurance Paid To
    DAT("Delivered at Terminal"),        // Delivered at Terminal
    DAP("Delivered at Place"),        // Delivered at Place
    DDP("Delivered Duty Paid"),        // Delivered Duty Paid
    OTH("Others");         // Others

    private final String displayText;

    private LCPriceTerm(final String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
