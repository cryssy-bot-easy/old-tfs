package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CustomerLog;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 */
public interface CustomerLogRepository {

    public void persist(CustomerLog customerLog);

    public void delete(TradeServiceId tradeServiceId);
}
