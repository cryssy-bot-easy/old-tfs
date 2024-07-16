package com.ucpb.tfs.domain.accounting.enumTypes;

/**
 * User: giancarlo
 * Date: 10/12/12
 * Time: 1:48 PM
 */
public enum AccountingEntryActualStatus {

    FOR_CHECKING("FOR_CHECKING"), FOR_POSTING("FOR_POSTING"), POSTED("POSTED");

    private final String code;

    AccountingEntryActualStatus(String code) {
        this.code = code;
    }
}
