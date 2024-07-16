package com.ucpb.tfs.domain.cdt.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/27/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TransactionTypeCode {

    CSH("CASH"), CHK("CHECK"), ADC("ADJBOC");

    private final String code;

    TransactionTypeCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
