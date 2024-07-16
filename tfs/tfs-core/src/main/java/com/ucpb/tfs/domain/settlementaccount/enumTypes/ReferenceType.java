package com.ucpb.tfs.domain.settlementaccount.enumTypes;

/**
 * @author Val
 */
public enum ReferenceType {

    CASH,
    CASA,
    CHECK,
    IBT_BRANCH,

    APPLY_TO_NEGO,
    APPLY_TO_LOAN,
    REFUND_TO_CLIENT_CREDIT_TO_CASA,
    REFUND_TO_CLIENT_ISSUE_MC,

//    TFS_SETUP_AP,
    TFS_SETUP_AP_PRODUCT,                     // From excess payments inside TFS
    TFS_SETUP_AP_SERVICE,                     // From excess payments inside TFS
    OUTSIDE_SETUP_AP,                 // From excess payments outside TFS
    APPLY_AP,

    TFS_SETUP_AR,                     // From inside TFS
    OUTSIDE_SETUP_AR,                 // From outside TFS
    APPLY_AR,

    APPLY_MD,

    REMITTANCE,
    REMITTANCE_RTGS_PDDTS,

    TR_LOAN,
    IB_LOAN,
    UA_LOAN,
    OUTGOING_MT;

//    UNPAY_AP,
//    UNPAY_AR;
}
