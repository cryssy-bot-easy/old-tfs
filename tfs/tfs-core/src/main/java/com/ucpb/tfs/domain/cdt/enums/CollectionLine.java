package com.ucpb.tfs.domain.cdt.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/27/13
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public enum CollectionLine {

    C("COLLECTION"), A("ADJUSTMENT");

    private final String code;

    CollectionLine(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
