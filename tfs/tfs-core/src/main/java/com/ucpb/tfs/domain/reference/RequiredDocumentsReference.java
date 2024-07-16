package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;

/**
 * User: Marv
 * Date: 10/31/12
 */

public class RequiredDocumentsReference {
    
    private Long id;
    
    private DocumentCode documentCode;
    
    private String description;
    
    private DocumentType documentType;

    public RequiredDocumentsReference() {
    }
    
    public RequiredDocumentsReference(DocumentCode documentCode, String description, DocumentType documentType) {
        this.documentCode = documentCode;
        this.description = description;
        this.documentType = documentType;
    }

}
