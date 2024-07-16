package com.ucpb.tfs.domain.documents;

import com.ucpb.tfs.domain.product.DocumentNumber;

import java.util.List;

/**
 * User: Marv
 * Date: 10/31/12
 */

public interface RequiredDocumentRepository {

    public void persist(RequiredDocument requiredDocument);

    public void merge(RequiredDocument requiredDocument);

    public void update(RequiredDocument requiredDocument);

    public RequiredDocument load(DocumentCode documentCode);

}
