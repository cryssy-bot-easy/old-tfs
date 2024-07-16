package com.ucpb.tfs.domain.report;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import com.ucpb.tfs.domain.report.enums.BankType;
import com.ucpb.tfs.domain.service.TradeServiceId;

public class DailyFunding {
	
	private long id;
	
	private TradeServiceId tradeServiceId;
	
	private String bank;
	
	private String bankName;
	
	private BankType bankType;
	
	private Currency currency;
	
	private BigDecimal amount;
	
	private Date settledDate;
	
	public DailyFunding(){}
	
	public DailyFunding(TradeServiceId tradeServiceId, String bank,
			String bankName, BankType bankType, String currency,
			BigDecimal amount, Date settledDate) {
		this.tradeServiceId = tradeServiceId;
		
		this.bank = bank;
		
		this.bankName = bankName;
		
		this.bankType = bankType;

		this.currency = Currency.getInstance(currency);
		
		this.amount = amount;
		
		this.settledDate = settledDate;
	}
	
	public long getId(){
		return id;
	}
	
	public TradeServiceId getTradeServiceId(){
		return tradeServiceId;
	}
	
	public String getBank(){
		return bank;
	}
	
	public BankType getBankType(){
		return bankType;
	}
	
	public Currency getCurrency(){
		return currency;
	}
	
	public BigDecimal getAmount(){
		return amount;
	}
	
	public Date getSettledDate(){
		return settledDate;
	}
}
