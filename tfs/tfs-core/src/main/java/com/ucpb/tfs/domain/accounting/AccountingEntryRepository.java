package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.accounting.enumTypes.AccountingEntryType;
import com.ucpb.tfs.domain.accounting.enumTypes.BookCurrency;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;

/**
 * User: giancarlo
 * Date: 10/2/12
 * Time: 11:41 AM
 */
public interface AccountingEntryRepository {
    public void save(AccountingEntry accountingEntry);

    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId);

    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, BookCurrency originalCurrency, BookCurrency settlementCurrency);

    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency);

    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency, AccountingEntryType accountingEntryType);

    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType);

    public List<AccountingEntry> getEntries(AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency, AccountingEntryType accountingEntryType);

    public HashMap<String,String> getEntriesAll();

    public List<String> getEntriesAllForChecking();

    public Long getCount();

    public void clear();

    public void updateAccountingCode(long id,String code);

    public void updateFormulaValue(long id,String code);

    public void updateFormulaPesoValue(long id,String code);

    public  void updateAccounting(String sql);


}
