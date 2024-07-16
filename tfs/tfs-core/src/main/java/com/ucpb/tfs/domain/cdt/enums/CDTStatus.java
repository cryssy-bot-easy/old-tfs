package com.ucpb.tfs.domain.cdt.enums;

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CDTStatus
 */
 

public enum CDTStatus {

    NEW("NEW"), PENDING("PENDING"), PAID("PAID"), REJECTED("REJECTED"),
    REFUNDED("REFUNDED"), FORREFUND("FORREFUND"),
    DORMANT("PENDING"), SENTTOBOC("BOC"), ABANDONED("ABANDONED"),REMITTED("REMITTED");

    private final String code;

    CDTStatus(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
