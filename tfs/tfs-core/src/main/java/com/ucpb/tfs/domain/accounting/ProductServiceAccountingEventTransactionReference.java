package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

/**
 * User: giancarlo
 * Date: 11/22/12
 * Time: 6:24 PM
 */
public class ProductServiceAccountingEventTransactionReference {
    private Long id;
    private AccountingEventTransactionId accountingEventTransactionId;
    private ProductId productId;
    private ServiceType serviceType;

    ProductServiceAccountingEventTransactionReference(){
    }
}
