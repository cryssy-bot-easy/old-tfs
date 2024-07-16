package com.ucpb.tfs.domain.cdt.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/27/13
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public enum CollectionChannel {

    OTC("OVER THE COUNTER"), ELE("ELECTRONIC");

    private final String code;

    CollectionChannel(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
