package com.ucpb.tfs.domain.product;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 7/12/12
 * @author Jett Gamboa
 */
public class DocumentNumber implements Serializable {

    private String documentNumber;

    public DocumentNumber() {}

    public DocumentNumber(final String documentNumber) {
        Validate.notNull(documentNumber);
        this.documentNumber = documentNumber;
    }

    @Override
    public String toString() {
        return documentNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentNumber that = (DocumentNumber) o;

        if (documentNumber != null ? !documentNumber.equals(that.documentNumber) : that.documentNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return documentNumber != null ? documentNumber.hashCode() : 0;
    }
}
