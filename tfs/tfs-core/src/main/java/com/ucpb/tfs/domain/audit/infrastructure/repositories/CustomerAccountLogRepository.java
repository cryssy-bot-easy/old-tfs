package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CustomerAccount;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 */
public interface CustomerAccountLogRepository {

    public void persist(CustomerAccount log);

    public void delete(TradeServiceId tradeServiceId);
	
	public void deleteBatchFlag(int flag);

}
