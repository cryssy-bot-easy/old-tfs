package com.ucpb.tfs.domain.service;

import org.apache.commons.lang.Validate;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 7/19/12
 */
public class TradeServiceId implements Serializable {

    private String tradeServiceId;

    public TradeServiceId() {
        UUIDGenerator uuidGenerator = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
        this.tradeServiceId = (String)uuidGenerator.generate(null, null);
    }

    public TradeServiceId(final String tradeServiceId) {
        Validate.notNull(tradeServiceId);
        this.tradeServiceId = tradeServiceId;
    }

    @Override
    public String toString() {
        return this.tradeServiceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeServiceId that = (TradeServiceId) o;

        if (tradeServiceId != null ? !tradeServiceId.equals(that.tradeServiceId) : that.tradeServiceId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tradeServiceId != null ? tradeServiceId.hashCode() : 0;
    }
}
