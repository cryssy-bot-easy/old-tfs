package com.ucpb.tfs.domain.product;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 9/22/12
 */
public class ICNumber implements Serializable {

    private String icNumber;

    public ICNumber() {}

    public ICNumber(final String icNumber) {
        Validate.notNull(icNumber);
        this.icNumber = icNumber;
    }

    @Override
    public String toString() {
        return icNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ICNumber icNumber1 = (ICNumber) o;

        if (!icNumber.equals(icNumber1.icNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return icNumber.hashCode();
    }
}
