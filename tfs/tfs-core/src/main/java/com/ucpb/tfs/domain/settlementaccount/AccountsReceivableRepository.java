package com.ucpb.tfs.domain.settlementaccount;

public interface AccountsReceivableRepository {

    public AccountsReceivable load(SettlementAccountNumber settlementAccountNumber);

    public AccountsReceivable load(String id);

    public void persist(AccountsReceivable ar);

    public void update(AccountsReceivable ar);

    public void merge(AccountsReceivable ar);

}
