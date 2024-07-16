package com.ucpb.tfs.domain.product;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: Marv
 * Date: 9/26/12
 */

public class IndemnityNumber implements Serializable {

    protected String indemnityNumber;

    protected IndemnityNumber() {}

    protected IndemnityNumber(final String id) {
        Validate.notNull(id);
        this.indemnityNumber = id;
    }

    @Override
    public String toString() {
        return indemnityNumber;
    }

}
