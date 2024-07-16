package com.ucpb.tfs.domain.security;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/28/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Position {

    private PositionCode code;

    private String positionName;

    private BigDecimal signingLimit;

    public Position() { }

    public Position(PositionCode code) {
        this.code = code;
    }

    public Position(PositionCode code, String positionName, BigDecimal signingLimit) {
        this.code = code;
        this.positionName = positionName;
        this.signingLimit = signingLimit;
    }

    public void setDetails(String positionName, BigDecimal signingLimit) {
        this.positionName = positionName;
        this.signingLimit = signingLimit;
    }

    public String getPositionName() {
        return positionName;
    }

    public BigDecimal getSigningLimit() {
        return signingLimit;
    }

}
