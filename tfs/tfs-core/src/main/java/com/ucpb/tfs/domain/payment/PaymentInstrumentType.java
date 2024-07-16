package com.ucpb.tfs.domain.payment;

/**
 * User: Jett
 * Date: 7/19/12
 */
public enum PaymentInstrumentType {

    CASA,
    CASH,
    CHECK,
    REMITTANCE,
    IBT_BRANCH,

    MD,
    APPLY_TO_NEGO,
    APPLY_TO_LOAN,
    REFUND_TO_CLIENT_CREDIT_TO_CASA,
    REFUND_TO_CLIENT_ISSUE_MC,

    AP,
    TFS_SETUP_AP,                     // From excess payments inside TFS
    OUTSIDE_SETUP_AP,                 // From excess payments outside TFS
    APPLY_AP,

    AR,
    TFS_SETUP_AR,                     // From inside TFS
    OUTSIDE_SETUP_AR,                 // From outside TFS
    APPLY_AR,

    TR_LOAN,
    IB_LOAN,
    UA_LOAN,
    DBP,    // I'm breaking the convention because renaming the select value from DBP to DBP_LOAN might break multiple js
    EBP,	// Added to distinguish between export bills and domestic bills 

    MC_ISSUANCE,
    SWIFT,
    PDDTS;

    public boolean isLoan(){
        return this.equals(TR_LOAN) || this.equals(IB_LOAN) || this.equals(UA_LOAN) || this.equals(DBP) || this.equals(EBP);
    }
}
