package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;

import java.util.List;

/**
 * User: Marv
 * Date: 10/31/12
 */

public interface RequiredDocumentsReferenceRepository {

    public void save(RequiredDocumentsReference requiredDocumentsReference);
    
    public RequiredDocumentsReference load(DocumentCode documentCode);

    public List<RequiredDocumentsReference> getRequiredDocuments(DocumentType documentType);

    public void clear();
    
}
