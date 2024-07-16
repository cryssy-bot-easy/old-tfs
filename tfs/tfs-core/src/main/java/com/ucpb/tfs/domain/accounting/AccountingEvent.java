package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: giancarlo
 * Date: 9/29/12
 * Time: 3:18 PM
 */
public class AccountingEvent implements Serializable {

    private AccountingEventId accountingEventId;
    private ProductId productId;
    private ServiceType serviceType;
    private AccountingEventTransactionId accountingEventTransactionId;

    private String currencyOriginal;
    private String currencySettlement;


    public AccountingEvent() {

    }

    public AccountingEvent(
            AccountingEventId accountingEventId,
            ProductId productId,
            ServiceType serviceType,
            AccountingEventTransactionId accountingEventTransactionId,
            String currencyOriginal,
            String currencySettlement
    ) {

        this.accountingEventId = accountingEventId;
        this.productId = productId;
        this.serviceType = serviceType;
        this.accountingEventTransactionId = accountingEventTransactionId;
        this.currencyOriginal = currencyOriginal;
        this.currencySettlement = currencySettlement;
    }

    public AccountingEventId getAccountingEventId() {
        return accountingEventId;
    }

}
