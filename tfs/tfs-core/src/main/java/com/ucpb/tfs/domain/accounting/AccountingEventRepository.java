package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;

/**
 * User: JAVA_training
 * Date: 9/30/12
 * Time: 3:05 PM
 */
public interface AccountingEventRepository {

    public void save(AccountingEvent accountingEvent);

    public List<AccountingEvent> getEvents(ProductId productId, ServiceType serviceType);

    public Long getCount();

    public void clear();
}
