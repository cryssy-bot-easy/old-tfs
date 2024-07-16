package com.ucpb.tfs.domain.accounting;

public class GlMapping {

    private Integer id;
    private String accountingCode;
    private String bookCode;
    private String bookCurrency;
    private String lbpAccountingCode;
    private String lbpParticulars;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }
    
    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }
    
    public String getBookCurrency() {
        return bookCurrency;
    }

    public void setBookCurrency(String bookCurrency) {
        this.bookCurrency = bookCurrency;
    }
    
    public String getLbpAccountingCode() {
        return lbpAccountingCode;
    }

    public void setLbpAccountingCode(String lbpAccountingCode) {
        this.lbpAccountingCode = lbpAccountingCode;
    }
    
    public String getLbpParticulars() {
        return lbpParticulars;
    }

    public void setLbpParticulars(String lbpParticulars) {
        this.lbpParticulars = lbpParticulars;
    }
}
