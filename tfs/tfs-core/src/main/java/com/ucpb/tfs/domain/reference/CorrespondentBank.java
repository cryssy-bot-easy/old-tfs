package com.ucpb.tfs.domain.reference;

import java.util.Currency;

/**
 */
public class CorrespondentBank {

    private Long id;

    private String bankCode;
    private Currency currency;
    private String bankName;
    private String cbCreditorCode;
    private String glBankCode;
    private String depositoryFlag;
    private String swiftFlag;
    private String swiftBankCode;
    private String bankGroupCode;
    private String swiftBranchCode;
    private String rbuAccount;
    private Integer accountType;
    private Integer countryCode;
    private Integer riskIndicator;
    private Integer counterpartyType;
    private Integer resEligibility;
    private Integer creditorCode;

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankGroupCode() {
        return bankGroupCode;
    }

    public void setBankGroupCode(String bankGroupCode) {
        this.bankGroupCode = bankGroupCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCbCreditorCode() {
        return cbCreditorCode;
    }

    public void setCbCreditorCode(String cbCreditorCode) {
        this.cbCreditorCode = cbCreditorCode;
    }

    public Integer getCounterpartyType() {
        return counterpartyType;
    }

    public void setCounterpartyType(Integer counterpartyType) {
        this.counterpartyType = counterpartyType;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getCreditorCode() {
        return creditorCode;
    }

    public void setCreditorCode(Integer creditorCode) {
        this.creditorCode = creditorCode;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getDepositoryFlag() {
        return depositoryFlag;
    }

    public void setDepositoryFlag(String depositoryFlag) {
        this.depositoryFlag = depositoryFlag;
    }

    public String getGlBankCode() {
        return glBankCode;
    }

    public void setGlBankCode(String glBankCode) {
        this.glBankCode = glBankCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRbuAccount() {
        return rbuAccount;
    }

    public void setRbuAccount(String rbuAccount) {
        this.rbuAccount = rbuAccount;
    }

    public Integer getResEligibility() {
        return resEligibility;
    }

    public void setResEligibility(Integer resEligibility) {
        this.resEligibility = resEligibility;
    }

    public Integer getRiskIndicator() {
        return riskIndicator;
    }

    public void setRiskIndicator(Integer riskIndicator) {
        this.riskIndicator = riskIndicator;
    }

    public String getSwiftBankCode() {
        return swiftBankCode;
    }

    public void setSwiftBankCode(String swiftBankCode) {
        this.swiftBankCode = swiftBankCode;
    }

    public String getSwiftBranchCode() {
        return swiftBranchCode;
    }

    public void setSwiftBranchCode(String swiftBranchCode) {
        this.swiftBranchCode = swiftBranchCode;
    }

    public String getSwiftFlag() {
        return swiftFlag;
    }

    public void setSwiftFlag(String swiftFlag) {
        this.swiftFlag = swiftFlag;
    }
}
