package com.ucpb.tfs.domain.cdt;


import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType;
import com.ucpb.tfs.utils.UtilSetFields;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CDTRemittance
 */

public class CDTRemittance {

    String id;

    Date processDate;

    String processingUnitCode;

    String transactionCode;

    Date remittanceDate;
    Date collectionFrom;
    Date collectionTo;

    BigDecimal totalRemitted;
    
    BigDecimal finalAmount;

    String bocAccount;

    PaymentRequestType paymentRequestType;

    public CDTRemittance() {}

    public CDTRemittance(PaymentRequestType paymentRequestType, Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);

        this.paymentRequestType = paymentRequestType;
        this.totalRemitted = new BigDecimal(details.get("orgremittanceAmount").toString().replaceAll(",", ""));
        this.finalAmount = new BigDecimal(details.get("remittanceAmount").toString().replaceAll(",", ""));
        this.collectionFrom = new Date(details.get("collectionPeriodFrom").toString());
        this.collectionTo = new Date(details.get("collectionPeriodTo").toString());

        this.processingUnitCode = (String) details.get("unitcode");
    }

    public Date getRemittanceDate() {
        return remittanceDate;
    }

    public BigDecimal getTotalRemitted() {
        return totalRemitted;
    }

    public String getBocAccount() {
        return bocAccount;
    }

    public Date getCollectionFrom() {
        return collectionFrom;
    }

    public Date getCollectionTo() {
        return collectionTo;
    }

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}


    
    
}
