package com.ucpb.tfs.domain.documents;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: Marv
 * Date: 10/31/12
 */

public class DocumentCode implements Serializable {

    private String documentCode;

    public DocumentCode() {}

    public DocumentCode(final String documentCode) {
        Validate.notNull(documentCode);
        this.documentCode = documentCode;
    }

    @Override
    public String toString() {
        return documentCode;
    }

}
