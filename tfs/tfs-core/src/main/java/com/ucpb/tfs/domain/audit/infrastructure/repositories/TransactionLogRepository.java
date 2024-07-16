package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.TransactionLog;
import com.ucpb.tfs.domain.service.TradeServiceId;

public interface TransactionLogRepository {

	public TransactionLog getAuditLog(Long id);
	
	public void persist(TransactionLog auditLog);
	
	public TransactionLog getAuditLogByReferenceNumber(String referenceNumber);

    public void delete(TradeServiceId tradeServiceId);
	
	public void deleteBatchFlag(int flag);

}
