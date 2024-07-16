package com.ucpb.tfs.domain.audit;

import com.ucpb.tfs.domain.service.TradeServiceId;

import java.util.Date;

/**
 */
public class CustomerAccount {

    private Long id;

    private TradeServiceId tradeServiceId;

    private Date dateCreated = new Date();

    private String customerNumber;

    private String accountNumber;
	
	private int batchFlag;
	
	public int getBatchFlag() {
		return batchFlag;
	}

	public void setBatchFlag(int batchFlag) {
		this.batchFlag = batchFlag;
	}

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
