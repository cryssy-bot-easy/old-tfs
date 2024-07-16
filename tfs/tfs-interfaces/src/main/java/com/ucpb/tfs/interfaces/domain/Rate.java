package com.ucpb.tfs.interfaces.domain;

import java.util.Date;

import com.ucpb.tfs.interfaces.domain.enums.RateType;

public class Rate {

	private String sourceCurrency;
	
	private String targetCurrency;
	
	private double rate;
	
	private RateType type;
	
	private Date expiryDate;

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public RateType getType() {
		return type;
	}

	public void setType(RateType type) {
		this.type = type;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getSourceCurrency() {
		return sourceCurrency;
	}

	public void setSourceCurrency(String sourceCurrency) {
		this.sourceCurrency = sourceCurrency;
	}

	public String getTargetCurrency() {
		return targetCurrency;
	}

	public void setTargetCurrency(String targetCurrency) {
		this.targetCurrency = targetCurrency;
	}
	
}
