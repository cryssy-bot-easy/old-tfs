package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

/**
 * User: giancarlo
 * Date: 10/9/12
 * Time: 4:31 PM
 */
public class AccountingDefaultAccountingCode {

    private String id;
    private ProductId productId;
    private ServiceType serviceType;
    private AccountingEventId accountingEventId;
    private AccountingEventTransactionId accountingEventTransactionId;


    private String formulaVariable;// how it will be called/identified/named in formula
    private String defaultAccountingCode; //GL Code

    public void AccountingDefaultAccountingCode(){};

    public AccountingDefaultAccountingCode(ProductId productId, ServiceType serviceType, AccountingEventId accountingEventId, AccountingEventTransactionId accountingEventTransactionId, String formulaVariable, String defaultAccountingCode) {
        this.productId = productId;
        this.serviceType = serviceType;
        this.accountingEventId = accountingEventId;
        this.accountingEventTransactionId = accountingEventTransactionId;
        this.formulaVariable = formulaVariable;
        this.defaultAccountingCode = defaultAccountingCode;
    }

    public AccountingDefaultAccountingCode(String formulaVariable, String defaultAccountingCode) {
        this.formulaVariable = formulaVariable;
        this.defaultAccountingCode = defaultAccountingCode;
        this.productId = null;
        this.serviceType = null;
        this.accountingEventId = null;
        this.accountingEventTransactionId = null;
    }

    public ProductId getProductId() {
        return productId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public AccountingEventId getAccountingEventId() {
        return accountingEventId;
    }

    public AccountingEventTransactionId getAccountingEventTransactionId() {
        return accountingEventTransactionId;
    }

    public String getFormulaVariable() {
        return formulaVariable;
    }

    public String getDefaultAccountingCode() {
        return defaultAccountingCode;
    }
}
