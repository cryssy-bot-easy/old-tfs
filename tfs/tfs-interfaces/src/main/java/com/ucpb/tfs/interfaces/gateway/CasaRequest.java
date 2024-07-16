package com.ucpb.tfs.interfaces.gateway;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CasaRequest  {

    public static final String EMPTY = "";
    private static final int MAX_LENGTH = 66;
    private static final String FILLER_CHAR = " ";

    private String username;

	private String password;
	
	private String userId;

	private TransactionCode transactionCode;

	private String accountNumber;

	private String branchCode;

    // MARV: added currency code for message string
    private Currency currency;

    protected abstract String doBuild();

    public String getUsername() {
        return username != null ? username : EMPTY;
    }

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
        return password != null ? password : EMPTY;
    }

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
        return userId != null ? userId : EMPTY;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public TransactionCode getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(TransactionCode transactionCode) {
		this.transactionCode = transactionCode;
	}

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = TransactionCode.valueOf(transactionCode);
    }

	public String getAccountNumber() {
        return accountNumber != null ? accountNumber : EMPTY;
    }

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBranchCode() {
        return branchCode != null ? branchCode : EMPTY;
    }

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public byte[] pack(String charSet) throws UnsupportedEncodingException{
		return toRequestString().getBytes(charSet);
	}

    public String toRequestString(){
        return StringUtils.rightPad(doBuild(),MAX_LENGTH,FILLER_CHAR);
    }

    @Override
    public String toString() {
        return toRequestString();
    }

    // MARV: added currency code for message string
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    // MARV: added currency code for message string
    public Currency getCurrency() {
        return currency;
    }

}
