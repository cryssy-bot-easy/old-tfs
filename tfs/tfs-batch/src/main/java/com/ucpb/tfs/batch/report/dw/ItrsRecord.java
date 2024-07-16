package com.ucpb.tfs.batch.report.dw;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class ItrsRecord {

	public int bankCode = 470;
	public int transactionDate;
	public String form;
	public String transactionCode;
	public Integer bookCode;
	public String currency;
	public BigDecimal amount;
	public String countryCode;
	public String exporterCode;
	public String tinNumber1;
	public String importerCode;
	public String commodityCode;
	public int paymentMode;
	public BigInteger importStatusCode;
	public String lcNumber;
	public int acceptanceInt;
	public Integer acceptanceDate;
	public Integer originalMaturity;
	public Integer newMaturity;
	public String remark;
	public String clientName;
	public String tinNumber2;
	public String particulars;
	public String tradeserviceId;
	public String documentNumber;
	public String referenceNumber;
	public Integer revId;

	public Integer getRevId() {
		return revId;
	}
	public BigInteger getImportStatusCode() {
		return importStatusCode;
	}

	public void setImportStatusCode(BigInteger importStatusCode) {
		this.importStatusCode = importStatusCode;
	}

	public void setRevId(Integer revId) {
		this.revId = revId;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public int getAcceptanceInt() {
		return acceptanceInt;
	}
	public void setAcceptanceInt(int acceptanceInt) {
		this.acceptanceInt = acceptanceInt;
	}
	public Integer getBankCode() {
		return bankCode;
	}
	public void setBankCode(int bankCode) {
		this.bankCode = bankCode;
	}
	public Integer getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(int transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public Integer getBookCode() {
		return bookCode;
	}
	public void setBookCode(int bookCode) {
		this.bookCode = bookCode;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getExporterCode() {
		return exporterCode;
	}
	public void setExporterCode(String exporterCode) {
		this.exporterCode = exporterCode;
	}
	public String getTinNumber1() {
		return tinNumber1;
	}
	public void setTinNumber1(String tinNumber1) {
		this.tinNumber1 = tinNumber1;
	}
	public String getImporterCode() {
		return importerCode;
	}
	public void setImporterCode(String importerCode) {
		this.importerCode = importerCode;
	}
	public String getCommodityCode() {
		return commodityCode;
	}
	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}
	public Integer getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(int paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getLcNumber() {
		return lcNumber;
	}
	public void setLcNumber(String lcNumber) {
		this.lcNumber = lcNumber;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public Integer getOriginalMaturity() {
		return originalMaturity;
	}
	public void setOriginalMaturity(Integer originalMaturity) {
		this.originalMaturity = originalMaturity;
	}
	public Integer getNewMaturity() {
		return newMaturity;
	}
	public void setNewMaturity(Integer newMaturity) {
		this.newMaturity = newMaturity;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getTinNumber2() {
		return tinNumber2;
	}
	public void setTinNumber2(String tinNumber2) {
		this.tinNumber2 = tinNumber2;
	}
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public String getTradeserviceId() {
		return tradeserviceId;
	}
	public void setTradeserviceId(String tradeserviceId) {
		this.tradeserviceId = tradeserviceId;
	}

	public void setAcceptanceDate(Integer acceptanceDate) {
		this.acceptanceDate = acceptanceDate;
	}

	public Integer getAcceptanceDate() {
		return acceptanceDate;

	}

}
