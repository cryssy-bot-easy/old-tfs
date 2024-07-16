package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: giancarlo
 * Date: 10/11/12
 * Time: 12:09 PM
 */
public interface AccountingEntryActualVariablesRepository {

    public void save(AccountingEntryActualVariables accountingEntryActualVariables);

    public List<AccountingEntryActualVariables> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransaction accountingEventTransaction, TradeServiceId tradeServiceId);

    public List<AccountingEntryActualVariables> getEntries(TradeServiceId tradeServiceId);

    public List<AccountingEntryActualVariables> getTransactionEntries(TradeServiceId tradeServiceId);

    public List<AccountingEntryActualVariables> getPaymentEntries(TradeServiceId tradeServiceId);

    public Long getCount();

    public void clear();

    public void delete(TradeServiceId tradeServiceId);

    public BigDecimal getTotalPesoCredit(TradeServiceId tradeServiceId);

    public BigDecimal getTotalPesoDebit(TradeServiceId tradeServiceId);

    public BigDecimal getTotalOriginalCredit(TradeServiceId tradeServiceId);

    public BigDecimal getTotalOriginalDebit(TradeServiceId tradeServiceId);

//    public List<AccountingEntryActual> getEntries(TradeServiceId tradeServiceId, String... accEvtRanId);
}
