package com.ucpb.tfs.domain.report;

import java.util.Date;
import java.util.List;

import com.ucpb.tfs.domain.service.TradeServiceId;

public interface DailyFundingRepository {

	public void persist(DailyFunding dailyFunding);
	
	public List<DailyFunding> getDailyFundingBySettledDate(Date date);
	
	public DailyFunding getDailyFundingById (Long id);
	
	public DailyFunding getDailyFundingByTradeServiceId (TradeServiceId tradeServiceId);
}
