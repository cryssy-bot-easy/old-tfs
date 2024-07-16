package com.ucpb.tfs.domain.reference;

import java.io.Serializable;

/**
 * User: Marv
 * Date: 11/10/12
 */

public class DocumentFormatReference implements Serializable {

    private Long id;

    private FormatCode formatCode;

    private String formatDescription;

    
    public DocumentFormatReference() {}
    
    public DocumentFormatReference(FormatCode formatCode, String formatDescription) {
        this.formatCode = formatCode;
        this.formatDescription = formatDescription;
    }
}
