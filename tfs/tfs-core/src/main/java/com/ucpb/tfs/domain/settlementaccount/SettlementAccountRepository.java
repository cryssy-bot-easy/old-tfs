package com.ucpb.tfs.domain.settlementaccount;

/**
 * User: IPCVal
 * Date: 8/31/12
 */
public interface SettlementAccountRepository {

    public SettlementAccount load(SettlementAccountNumber settlementAccountNumber);

    public void persist(SettlementAccount settlementAccount);

    public void update(SettlementAccount settlementAccount);
}
