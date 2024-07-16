package com.ucpb.tfs.batch.report.dw;

import java.math.BigDecimal;
import java.util.Date;

public class LetterOfCredit {
	
	
	private String documentNumber;
	
	private Date dateClosed;
	
	private Status status;
	
	private BigDecimal outstandingBalance;
	
	private BigDecimal averageDailyBalance;

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public Date getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public BigDecimal getOutstandingBalance() {
		return outstandingBalance;
	}

	public void setOutstandingBalance(BigDecimal outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	public BigDecimal getAverageDailyBalance() {
		return averageDailyBalance;
	}

	public void setAverageDailyBalance(BigDecimal averageDailyBalance) {
		this.averageDailyBalance = averageDailyBalance;
	}
	
	
	

}
