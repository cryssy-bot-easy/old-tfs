package com.ucpb.tfs.domain.settlementaccount;

import com.incuventure.ddd.domain.annotations.DomainFactory;

/**
 * User: Val
 * Date: 7/22/12
 */
@DomainFactory
public interface SettlementAccountFactory {

    public ISettlementAccount createMarginalDeposit(SettlementAccountNumber settlementAccountNumber);

//    public ISettlementAccount createAccountsPayable(SettlementAccountNumber settlementAccountNumber);

//    public ISettlementAccount createAccountsReceivable(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createCasa(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createCash(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createCheck(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createIbtBranch(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createTrLoan(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createIbLoan(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createUaLoan(SettlementAccountNumber settlementAccountNumber);

    public ISettlementAccount createRemittance(SettlementAccountNumber settlementAccountNumber);
}
