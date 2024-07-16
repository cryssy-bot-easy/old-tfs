package com.ucpb.tfs.domain.security;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/28/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PositionCode implements Serializable {

    private String code;

    public PositionCode() {}

    public PositionCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
