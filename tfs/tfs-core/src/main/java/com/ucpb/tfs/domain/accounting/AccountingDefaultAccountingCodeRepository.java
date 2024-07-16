package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;

/**
 * User: giancarlo
 * Date: 10/9/12
 * Time: 4:42 PM
 */
public interface AccountingDefaultAccountingCodeRepository {

    public List<AccountingDefaultAccountingCode> getAccountingCodeDefaults(ProductId productId, ServiceType serviceType, AccountingEventId accountingEventId, AccountingEventTransactionId accountingEventTransactionId);

    public List<AccountingDefaultAccountingCode> getAccountingCodeDefaults();

    public Long getCount();

    public void clear();
}
