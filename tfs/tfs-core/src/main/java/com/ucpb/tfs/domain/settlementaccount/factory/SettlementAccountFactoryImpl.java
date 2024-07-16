package com.ucpb.tfs.domain.settlementaccount.factory;

import com.ucpb.tfs.domain.settlementaccount.*;
import org.springframework.stereotype.Component;

/**
 * User: Val
 * Date: 7/22/12
 */
@Component
public class SettlementAccountFactoryImpl implements SettlementAccountFactory {

    public ISettlementAccount createMarginalDeposit(SettlementAccountNumber settlementAccountNumber) {
        return new MarginalDeposit(settlementAccountNumber);
    }

//    public ISettlementAccount createAccountsPayable(SettlementAccountNumber settlementAccountNumber) {
//        return new AccountsPayable(settlementAccountNumber);
//    }

//    public ISettlementAccount createAccountsReceivable(SettlementAccountNumber settlementAccountNumber) {
//        return new AccountsReceivable(settlementAccountNumber);
//    }

    public ISettlementAccount createCasa(SettlementAccountNumber settlementAccountNumber) {
        return new Casa(settlementAccountNumber);
    }

    public ISettlementAccount createCash(SettlementAccountNumber settlementAccountNumber) {
        return new Cash(settlementAccountNumber);
    }

    public ISettlementAccount createCheck(SettlementAccountNumber settlementAccountNumber) {
        return new Check(settlementAccountNumber);
    }

    public ISettlementAccount createIbtBranch(SettlementAccountNumber settlementAccountNumber) {
        return new IbtBranch(settlementAccountNumber);
    }

    public ISettlementAccount createTrLoan(SettlementAccountNumber settlementAccountNumber) {
        return new TrLoan(settlementAccountNumber);
    }

    public ISettlementAccount createIbLoan(SettlementAccountNumber settlementAccountNumber) {
        return new IbLoan(settlementAccountNumber);
    }

    public ISettlementAccount createUaLoan(SettlementAccountNumber settlementAccountNumber) {
        return new UaLoan(settlementAccountNumber);
    }

    public ISettlementAccount createRemittance(SettlementAccountNumber settlementAccountNumber) {
        return new Remittance(settlementAccountNumber);
    }
}
