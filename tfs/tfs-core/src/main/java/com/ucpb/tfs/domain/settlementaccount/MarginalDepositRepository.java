package com.ucpb.tfs.domain.settlementaccount;

import java.util.Map;

public interface MarginalDepositRepository {

    public MarginalDeposit load(SettlementAccountNumber settlementAccountNumber);

    public void persist(MarginalDeposit md);

    public void update(MarginalDeposit md);

    public Map loadToMap(SettlementAccountNumber settlementAccountNumber);
}
