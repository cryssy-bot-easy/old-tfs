package com.ucpb.tfs.domain.settlementaccount;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 8/2/12
 */
public class SettlementAccountNumber implements Serializable {

    private String settlementAccountNumber;

    public SettlementAccountNumber() {}

    public SettlementAccountNumber(final String settlementAccountNumber) {
        Validate.notNull(settlementAccountNumber);
        this.settlementAccountNumber = settlementAccountNumber;
    }

    @Override
    public String toString() {
        return this.settlementAccountNumber;
    }
}
