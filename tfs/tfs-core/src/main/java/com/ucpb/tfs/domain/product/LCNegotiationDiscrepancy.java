package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.LCNegotiationDiscrepancyStatus;
import com.ucpb.tfs.utils.UtilSetFields;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
(revision)
SCR/ER Number: 
SCR/ER Description: 
[Revised by:] John Patrick C. Bautista
[Date revised:] July 27, 2017
[Date deployed:] 
Program [Revision] Details: Added new fields for LC Nego Discrepancy.
Member Type: Java
Project: tfs-core
Project Name: LCNegotiationDiscrepancy.java
 */

/**
 * User: IPCVal
 * Date: 10/22/12
 */
@Audited
//(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) // Not audited for now by Envers
public class LCNegotiationDiscrepancy implements Serializable {

    private ICNumber icNumber;

    private DocumentNumber documentNumber;

    private LCNegotiationDiscrepancyStatus lcNegotiationDiscrepancyStatus;

    private BigDecimal negotiationAmount;
    private Currency negotiationCurrency;

    private String negotiationBank;
    private String negotiationBankRefNumber;

    private String senderToReceiverInformation;

    private Boolean expiredLc;
    private Boolean overdrawnFor;
    private BigDecimal overdrawnAmount;
    private Boolean descriptionOfGoodsNotPerLc;
    private Boolean documentsNotPresented;
    private Boolean others;
    private String othersNarrative;
    
    private Date icDate;
    private String reasonForCancellation;
    private BigDecimal regularAmount;
    private BigDecimal cashAmount;

    private Date lastModifiedDate;
    

    public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public LCNegotiationDiscrepancy() {
        this.lcNegotiationDiscrepancyStatus = LCNegotiationDiscrepancyStatus.MARV;
        this.lastModifiedDate = new Date();
    }

    public LCNegotiationDiscrepancy(
    		DocumentNumber documentNumber,
            ICNumber icNumber,
            BigDecimal negotiationAmount,
            Currency negotiationCurrency,
            String negotiationBank,
            String negotiationBankRefNumber,
            String senderToReceiverInformation,
            Boolean expiredLc,
            Boolean overdrawnFor,
            BigDecimal overdrawnAmount,
            Boolean descriptionOfGoodsNotPerLc,
            Boolean documentsNotPresented,
            Boolean others,
            String othersNarrative
    ) {
        this.documentNumber = documentNumber;
        this.icNumber = icNumber;
        this.negotiationAmount = negotiationAmount;
        this.negotiationCurrency = negotiationCurrency;
        this.negotiationBank = negotiationBank;
        this.negotiationBankRefNumber = negotiationBankRefNumber;
        this.senderToReceiverInformation = senderToReceiverInformation;
        this.expiredLc = expiredLc;
        this.overdrawnFor = overdrawnFor;
        this.overdrawnAmount = overdrawnAmount;
        this.descriptionOfGoodsNotPerLc = descriptionOfGoodsNotPerLc;
        this.documentsNotPresented = documentsNotPresented;
        this.others = others;
        this.othersNarrative = othersNarrative;
        this.lcNegotiationDiscrepancyStatus = LCNegotiationDiscrepancyStatus.OPEN;
    }

    public void updateDetails(Map<String, Object> details) {
        System.out.println("\nLCNegotiationDiscrepancy.updateDetails() ===========\n");
        UtilSetFields.copyMapToObject(this, (HashMap) details);
        this.lastModifiedDate = new Date();
    }

    public void closeNegotiationDiscrepancy() {
        this.lcNegotiationDiscrepancyStatus = LCNegotiationDiscrepancyStatus.CLOSED;
        this.lastModifiedDate = new Date();
    }

    public void setToOpenNegotiationDiscrepancy() {
        this.lcNegotiationDiscrepancyStatus = LCNegotiationDiscrepancyStatus.OPEN;
        this.lastModifiedDate = new Date();
    }

    public ICNumber getIcNumber() {
        return icNumber;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public LCNegotiationDiscrepancyStatus getLcNegotiationDiscrepancyStatus() {
        return lcNegotiationDiscrepancyStatus;
    }

    public BigDecimal getNegotiationAmount() {
        return negotiationAmount;
    }

    public Currency getNegotiationCurrency() {
        return negotiationCurrency;
    }

    public String getNegotiationBank() {
        return negotiationBank;
    }

    public String getNegotiationBankRefNumber() {
        return negotiationBankRefNumber;
    }

    public String getSenderToReceiverInformation() {
        return senderToReceiverInformation;
    }

    public Boolean getExpiredLc() {
        return expiredLc;
    }

    public Boolean getOverdrawnFor() {
        return overdrawnFor;
    }

    public BigDecimal getOverdrawnAmount() {
        return overdrawnAmount;
    }

    public Boolean getDescriptionOfGoodsNotPerLc() {
        return descriptionOfGoodsNotPerLc;
    }

    public Boolean getDocumentsNotPresented() {
        return documentsNotPresented;
    }

    public Boolean getOthers() {
        return others;
    }

    public String getOthersNarrative() {
        return othersNarrative;
    }

    // 07242017
	public Date getIcDate() {
		return icDate;
	}

	public void setIcDate(Date icDate) {
		this.icDate = icDate;
	}

	public String getReasonForCancellation() {
		return reasonForCancellation;
	}

	public void setReasonForCancellation(String reasonForCancellation) {
		this.reasonForCancellation = reasonForCancellation;
	}

	public BigDecimal getRegularAmount() {
		return regularAmount;
	}

	public void setRegularAmount(BigDecimal regularAmount) {
		this.regularAmount = regularAmount;
	}

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}
	
}
