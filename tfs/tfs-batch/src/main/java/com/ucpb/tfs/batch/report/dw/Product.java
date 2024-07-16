package com.ucpb.tfs.batch.report.dw;

import java.math.BigDecimal;
import java.util.Date;

public class Product {
	
	private String documentNumber;
	
	private BigDecimal outstandingBalance;
	
	private Date entryDate;
	
	private String status;

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public BigDecimal getOutstandingBalance() {
		if(!"ACTIVE".equals(status)){
			return BigDecimal.ZERO;
		}
		return outstandingBalance;
	}

	public void setOutstandingBalance(BigDecimal outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
