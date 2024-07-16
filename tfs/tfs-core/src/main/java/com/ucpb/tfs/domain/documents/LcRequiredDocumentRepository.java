package com.ucpb.tfs.domain.documents;

import com.ucpb.tfs.domain.product.DocumentNumber;

import java.util.Set;

/**
 * User: Marv
 * Date: 10/31/12
 */

public interface LcRequiredDocumentRepository {

    public void persist(LcRequiredDocument lcRequiredDocument);

    public void merge(LcRequiredDocument lcRequiredDocument);

    public void update(LcRequiredDocument lcRequiredDocument);

    public LcRequiredDocument load(DocumentCode documentCode);

    public Set<LcRequiredDocument> load(DocumentNumber documentNumber);
}
