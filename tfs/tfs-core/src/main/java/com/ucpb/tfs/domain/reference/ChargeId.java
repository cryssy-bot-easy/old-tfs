package com.ucpb.tfs.domain.reference;

import org.apache.commons.lang.Validate;
import org.hibernate.type.StringType;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 7/12/12
 */
public class ChargeId extends StringType implements Serializable {

    private String chargeId;

    public ChargeId() {}

    public ChargeId(final String chargeId) {
        Validate.notNull(chargeId);
        this.chargeId = chargeId;
    }

    @Override
    public String toString() {
        return this.chargeId;
    }
}
