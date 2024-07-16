package com.ucpb.tfs.domain.service;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 9/12/12
 */
public class TradeServiceReferenceNumber implements Serializable {

    private String tradeServiceReferenceNumber;

    public TradeServiceReferenceNumber() {}

    public TradeServiceReferenceNumber(final String tradeServiceReferenceNumber) {
        Validate.notNull(tradeServiceReferenceNumber);
        this.tradeServiceReferenceNumber = tradeServiceReferenceNumber;
    }

    @Override
    public String toString() {
        return tradeServiceReferenceNumber;
    }

    public String getTradeServiceReferenceNumber() {
        return tradeServiceReferenceNumber;
    }
}
