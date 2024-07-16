package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;

public interface ProductReferenceRepository {

    public void save(ProductReference productReference);

    public ProductReference find(DocumentClass documentClass, DocumentType documentType,DocumentSubType1 documentSubType1, DocumentSubType2 documentSubType2);

    public Long getCount();

    public ProductReference find(ProductId productId);
    
    public String getUCPBProdID(String productId);
}
