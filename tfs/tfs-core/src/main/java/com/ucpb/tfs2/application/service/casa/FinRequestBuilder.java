package com.ucpb.tfs2.application.service.casa;

import com.ucpb.tfs.interfaces.gateway.FinRequest;
import com.ucpb.tfs.interfaces.gateway.TransactionCode;

import java.math.BigDecimal;
import java.util.Currency;

/**
 */
public class FinRequestBuilder {

    private String accountNumber;

    private String userId;

    private String username;

    private String password;

    private TransactionCode transactionCode;

    private Currency currency;

    private BigDecimal amount;

    private String branchCode;


    public FinRequestBuilder withAccountNumber(String accountNumber){
        this.accountNumber = accountNumber;
        return this;
    }

    public FinRequestBuilder withUserId(String userId){
        this.userId = userId;
        return this;
    }

    public FinRequestBuilder withUsername(String username){
        this.username = username;
        return this;
    }

    public FinRequestBuilder withPassword(String password){
        this.password = password;
        return this;
    }

    public FinRequestBuilder withTransactionCode(TransactionCode transactionCode){
        this.transactionCode = transactionCode;
        return this;
    }

    public FinRequestBuilder withCurrency(Currency currency){
        this.currency = currency;
        return this;
    }

    public FinRequestBuilder withAmount(BigDecimal amount){
        this.amount = amount;
        return this;
    }

    public FinRequestBuilder withBranchCode(String branchCode){
        this.branchCode = branchCode;
        return this;
    }

    public FinRequest build(){
        FinRequest finRequest = new FinRequest();
        finRequest.setAccountNumber(accountNumber);
        finRequest.setCurrency(currency);
        finRequest.setAmount(amount);
        finRequest.setBranchCode(branchCode);
        finRequest.setPassword(password);
        finRequest.setTransactionCode(transactionCode);
        finRequest.setUserId(userId);
        finRequest.setUsername(username);
        return  finRequest;
    }

}
