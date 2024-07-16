package com.ucpb.tfs.domain.reference;

import java.math.BigDecimal;
import java.util.Date;

/**
 */
public class DailyBalance {

    private Long id;

    private String documentNumber;

    private BigDecimal balance;

    private Date balanceDate;

    private BigDecimal originalBalance;
    
    private BigDecimal revalRate;
    
    private String currency;
    
    private String productId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(Date balanceDate) {
        this.balanceDate = balanceDate;
    }
    
    public BigDecimal getOriginalBalance() {
		return originalBalance;
	}

	public void setOriginalBalance(BigDecimal originalBalance) {
		this.originalBalance = originalBalance;
	}

	public BigDecimal getRevalRate() {
		return revalRate;
	}

	public void setRevalRate(BigDecimal revalRate) {
		this.revalRate = revalRate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
}
