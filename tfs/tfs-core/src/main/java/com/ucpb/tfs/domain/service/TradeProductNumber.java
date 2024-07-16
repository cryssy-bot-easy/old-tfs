package com.ucpb.tfs.domain.service;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 2/1/13
 */
public class TradeProductNumber implements Serializable {

    private String tradeProductNumber;

    public TradeProductNumber() {}

    public TradeProductNumber(final String tradeProductNumber) {
        Validate.notNull(tradeProductNumber);
        this.tradeProductNumber = tradeProductNumber;
    }

    @Override
    public String toString() {
        return tradeProductNumber;
    }
}
