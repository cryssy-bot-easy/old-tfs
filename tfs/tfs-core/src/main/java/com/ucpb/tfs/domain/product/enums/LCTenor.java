package com.ucpb.tfs.domain.product.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Geek
 * Date: 6/2/12
 * Time: 12:34 AM
 * To change this template use File | Settings | File Templates.
 */
public enum LCTenor {
    SIGHT("Sight"), USANCE("Usance"); //, OTHER("Other");

    private final String displayText;

    private LCTenor(final String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

}
