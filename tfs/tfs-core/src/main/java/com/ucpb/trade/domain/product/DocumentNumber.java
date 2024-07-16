package com.ucpb.trade.domain.product;

import org.apache.commons.lang.Validate;

/**
 * User: Jett
 * Date: 7/12/12
 * @author Jett Gamboa
 */

public class DocumentNumber {

    String id;

    public DocumentNumber(final String id) {
        Validate.notNull(id);
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

}
