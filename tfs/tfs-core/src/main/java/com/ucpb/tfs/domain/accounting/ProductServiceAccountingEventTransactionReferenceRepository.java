package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;

/**
 * User: giancarlo
 * Date: 11/23/12
 * Time: 11:22 PM
 */
public interface ProductServiceAccountingEventTransactionReferenceRepository {

    public void save(ProductServiceAccountingEventTransactionReference productServiceAccountingEventTransactionReference);

    public List<ProductServiceAccountingEventTransactionReference> getProductServiceAccountingEventTransactionReference(ProductId productId, ServiceType serviceType);

    public List<ProductServiceAccountingEventTransactionReference> getProductServiceAccountingEventTransactionReference();

    public Long getCount();

    public void clear();
}
