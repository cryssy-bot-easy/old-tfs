package com.ucpb.trade.domain.service;

import org.apache.commons.lang.Validate;

/**
 * User: Jett
 * Date: 7/12/12
 */
public class ChargeId {

    private String id;

    public ChargeId(final String id) {
        Validate.notNull(id);
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
