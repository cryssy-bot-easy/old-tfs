package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.AccountLog;
import com.ucpb.tfs.domain.service.TradeServiceId;

import java.util.Date;
import java.util.List;

public interface AccountLogRepository {

	public AccountLog getAccountLogById(Long id);
	
	public void persist(AccountLog accountLog);
	
	public List<AccountLog> getAccountLogsByOpeningDate(Date date);

    public List<AccountLog> getAccountLogsByCdtClosingDate(Date from, Date to);

	public AccountLog getAccountLogByAccountNumber(String accountNumber);

    public void delete(TradeServiceId tradeServiceId);
	
	public void deleteBatchFlag(int flag);

}
