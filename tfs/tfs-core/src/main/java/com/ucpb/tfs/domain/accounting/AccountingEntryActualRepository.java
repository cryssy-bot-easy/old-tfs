package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: giancarlo
 * Date: 10/11/12
 * Time: 12:09 PM
 */
public interface AccountingEntryActualRepository {

	public List<AccountingEntryActual> getAllByDate(Date dateFrom, Date dateTo);

    public void save(AccountingEntryActual accountingEntryActual);

    public List<AccountingEntryActual> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransaction accountingEventTransaction, TradeServiceId tradeServiceId);

    public List<AccountingEntryActual> getEntries(TradeServiceId tradeServiceId);

    public List<AccountingEntryActual> getTransactionEntries(TradeServiceId tradeServiceId);

    public List<AccountingEntryActual> getPaymentEntries(TradeServiceId tradeServiceId);

    public Long getCount();

    public void clear();

    public void delete(TradeServiceId tradeServiceId);

    public BigDecimal getTotalPesoCredit(TradeServiceId tradeServiceId);

    public BigDecimal getTotalPesoDebit(TradeServiceId tradeServiceId);

    public BigDecimal getTotalOriginalCredit(TradeServiceId tradeServiceId);

    public BigDecimal getTotalOriginalDebit(TradeServiceId tradeServiceId);
    
    public void updateIsPosted(String tradeServiceId, Boolean isPosted);
	
	public void updateWithError(String tradeServiceId);
    
    public BigDecimal getAllTotalOrigDebit (String postingDate);
    
    public BigDecimal getAllTotalOrigCredit (String postingDate);
    
    public BigDecimal getAllTotalPesoDebit (String postingDate);
    
    public BigDecimal getAllTotalPesoCredit (String postingDate);
    
    public void updateIsPostedTrue(Boolean isPostedValue);

//    public List<AccountingEntryActual> getEntries(TradeServiceId tradeServiceId, String... accEvtRanId);

    public List<GlMapping> getGlMapping(String tradeServiceId);
}
