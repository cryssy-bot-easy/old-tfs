package com.ucpb.tfs.domain.product;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 9/22/12
 */
public class NegotiationNumber implements Serializable {

    private String negotiationNumber;

    public NegotiationNumber() {}

    public NegotiationNumber(final String negotiationNumber) {
        Validate.notNull(negotiationNumber);
        this.negotiationNumber = negotiationNumber;
    }

    @Override
    public String toString() {
        return negotiationNumber;
    }
}
