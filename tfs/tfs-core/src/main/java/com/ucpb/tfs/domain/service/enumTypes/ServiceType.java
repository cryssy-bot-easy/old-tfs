
package com.ucpb.tfs.domain.service.enumTypes;

/**
 * User: Jett
 * Date: 7/24/12
 */
public enum ServiceType {

    OPENING, AMENDMENT, ADJUSTMENT, ISSUANCE, CANCELLATION, NEGOTIATION, NEGOTIATION_DISCREPANCY, SETTLEMENT,
    UA_LOAN_MATURITY_ADJUSTMENT, UA_LOAN_SETTLEMENT,

    // For Documents against Acceptance
    NEGOTIATION_ACCEPTANCE, NEGOTIATION_ACKNOWLEDGEMENT,

    // For SettlementAccount: MD/AP/AR
    COLLECTION, APPLICATION, REFUND, SETUP, APPLY, SETTLE,

    // for other imports and CDT
    PAYMENT,
    PAYMENT_OTHER,
    COMMON,
    CREATE,
    REMITTANCE,

     // reversal stuff (max 30 chars)
    OPENING_REVERSAL,
    SETTLEMENT_REVERSAL,
    UA_LOAN_SETTLEMENT_REVERSAL,

    OPENING_ADVISING,

    AMENDMENT_ADVISING,

    CANCELLATION_ADVISING,

    // for rebates
    PROCESS,
    REBATE;
}
