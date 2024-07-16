package com.ucpb.tfs.domain.settlementaccount;

public interface AccountsPayableRepository {

    public AccountsPayable load(SettlementAccountNumber settlementAccountNumber);

    public AccountsPayable load(String id);
    
    public AccountsPayable load(SettlementAccountNumber settlementAccountNumber, String id);

    public void persist(AccountsPayable ap);

    public void update(AccountsPayable ap);

    public void merge(AccountsPayable ap);

}
