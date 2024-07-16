package com.ucpb.tfs.domain.cdt;


import com.ucpb.tfs.domain.cdt.enums.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


//  PROLOGUE:
// 	(revision)
//	SCR/ER Number: SCR# IBD-16-1206-01
//	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
//	[Created by:] Allan Comboy and Lymuel Saul
//	[Date Deployed:] 12/20/2016
//	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
//	PROJECT: CORE
//	MEMBER TYPE  : Java
//	Project Name: CDTPaymentRequest

//  PROLOGUE:
// 	(revision)
//	SCR/ER Number:
//	SCR/ER Description:
//	[Updated by:] Cedrick Nungay
//	[Date Deployed:] 01/11/2018
//	Program [Revision] Details: Added importersEmail, rmbmEmail and branchEmail properties
//	PROJECT: CORE
//	MEMBER TYPE  : Java
//	Project Name: CDTPaymentRequest


public class CDTPaymentRequest {

    String iedieirdNumber;
    Date pchcDateReceived;

    String agentBankCode;

    String clientName;

    BigDecimal amount;

    PaymentRequestType paymentRequestType;

    //E2MStatus e2mStatus;
    String e2mStatus;
    CDTStatus status;

    BigDecimal finalDutyAmount;
    BigDecimal finalTaxAmount;
    BigDecimal finalCharges;
    BigDecimal ipf;

    BigDecimal amountCollected;
    BigDecimal e2mAmountCollected;

    Date dateUploaded;
    Date dateAbandoned;
    Date datePaid;
    Date datePaymentHistoryUploaded;

    String transactionReferenceNumber;
    String paymentReferenceNumber;

    String documentNumber;

    Boolean emailed;

    Date dutiesAndTaxesRemittedDate;
    Date IPFRemittedDate;

    Date dateSent;

    BigDecimal bankCharge;

    CollectionLine collectionLine;

    String collectionAgencyCode;

    CollectionType collectionType;

    CollectionChannel collectionChannel;

    TransactionTypeCode transactionTypeCode;

    String unitCode;

    BigDecimal paymentHistoryTotal;

    String cifNumber;

    String transactionCode;

    Boolean isRemitted;

    String allocationUnitCode;


    Date confDate;
    
	Date dateRemitted;
	
	Date dateRefunded;
	
	Date forRefundDate;
	
	String branchUnitCode;
	
//	Date pchcConfirmationDate;
	
	String exceptionCode;
	
	String officerCode;
	
	Set<EmailNotif> emailNotifs;
	
//	EmailNotif emailNotifs;

    private String importersEmail;
    private String rmbmEmail;
    private String branchEmail;

    CDTPaymentRequest() {
        this.emailed = false;
    }
    
    public void setEmailed(Boolean emailed) {
		this.emailed = emailed;
	}

    public CDTPaymentRequest(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
        this.emailed = false;
    }

    public void setAbandonedDate(Date dateAbandoned) {
        this.dateAbandoned = dateAbandoned;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }

//    public void setE2mStatus(String e2mStatus) {
//        this.e2mStatus = E2MStatus.valueOf(e2mStatus);
//    }

    public void setE2mStatus(String e2mStatus) {
        this.e2mStatus = e2mStatus;
    }

    public void updatePaymentHistoryUpdatedDate() {
        // when a payment history file is uploaded for this record, we set the date of the record to today
        this.datePaymentHistoryUploaded = new Date();
    }

    public void setStatus(CDTStatus status) {
        this.status = status;
    }

    public void refundPayment() {
		this.status = CDTStatus.REFUNDED;
        this.collectionLine = CollectionLine.A;
        this.dateRefunded = new Date();
//        this.paymentReferenceNumber = null;
        this.isRemitted = Boolean.FALSE;
//        this.dateSent = null;
        this.transactionTypeCode = TransactionTypeCode.ADC;
    }
    
    public void forRefundPayment() {
		this.status = CDTStatus.FORREFUND;
        this.collectionLine = CollectionLine.A;
        this.forRefundDate = new Date();
//        this.paymentReferenceNumber = null;
        this.isRemitted = Boolean.FALSE;
//        this.dateSent = null;
        this.transactionTypeCode = TransactionTypeCode.ADC;
    }

    public void setPaymentRequestType(PaymentRequestType paymentRequestType) {
        this.paymentRequestType = paymentRequestType;
    }

    public void setPaymentReferenceNumber(String paymentReferenceNumber) {
        this.paymentReferenceNumber = paymentReferenceNumber;
    }

    public void clearPaymentReferenceNumber() {
        this.paymentReferenceNumber = "";
    }

//    public E2MStatus getE2mStatus() {
//        return e2mStatus;
//    }

    public String getE2mStatus() {
        return e2mStatus;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentRequestType getPaymentRequestType() {
        return paymentRequestType;
    }

    public CDTStatus getStatus() {
        return status;
    }

    public BigDecimal getFinalDutyAmount() {
        return finalDutyAmount;
    }

    public BigDecimal getFinalTaxAmount() {
        return finalTaxAmount;
    }

    public BigDecimal getFinalCharges() {
        return finalCharges;
    }

    public BigDecimal getIpf() {
        return ipf;
    }

    public BigDecimal getAmountCollected() {
        return amountCollected;
    }

    public BigDecimal getE2mAmountCollected() {
        return e2mAmountCollected;
    }

    public Date getDutiesAndTaxesRemittedDate() {
        return dutiesAndTaxesRemittedDate;
    }

    public Date getIPFRemittedDate() {
        return IPFRemittedDate;
    }
    
    public void setIPFRemittedDate(Date ipfRemittedDate) {		
        this.IPFRemittedDate = ipfRemittedDate;		
    }

    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public Date getDatePaymentHistoryUploaded() {
        return datePaymentHistoryUploaded;
    }
    
    
    
    public void setDatePaymentHistoryUploaded(Date datePaymentHistoryUploaded) {
		this.datePaymentHistoryUploaded = datePaymentHistoryUploaded;
	}

	public Date getDatePaid() {
        return datePaid;
    }

    public Date getDateAbandoned() {
        return dateAbandoned;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public Date getPchcDateReceived() {
        return pchcDateReceived;
    }

    public String getPaymentReferenceNumber() {
        return paymentReferenceNumber;
    }

    public Boolean getEmailed() {
        return emailed;
    }

    public String getAgentBankCode() {
        return agentBankCode;
    }

    public Date getDateSent() {
        return dateSent;
    }

	public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }
	
	public void setDutiesAndTaxesRemittedDate(Date DutiesAndTaxesRemittedDate) {		
		
        this.dutiesAndTaxesRemittedDate = DutiesAndTaxesRemittedDate;		
    }

    public void setDatePaid(Date datePaid) {
        this.datePaid = datePaid;
    }

    public void setBankCharge(BigDecimal bankCharge) {
        this.bankCharge = bankCharge;
    }

    public BigDecimal getBankCharge() {
        return bankCharge;
    }
    
    public BigDecimal getBankChargeViaIedieirdNumber(String iedieirdNumber) {
    	this.iedieirdNumber = iedieirdNumber;
    	
        return bankCharge;
    }

    public void setAdditionalDetails(String collectionLine,
                                     String collectionAgencyCode,
                                     String collectionChannel) {

        this.collectionLine = CollectionLine.valueOf(collectionLine);
        this.collectionAgencyCode = collectionAgencyCode;
//        this.collectionType = getCollectionTypePerPaymentRequest();
        this.collectionChannel = CollectionChannel.valueOf(collectionChannel);
    }

    public void setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
    }

//    private CollectionType getCollectionTypePerPaymentRequest() {
//        switch (paymentRequestType) {
//            case FINAL:
//                return CollectionType.BOC1;
//
//            case ADVANCE:
//                return CollectionType.BOC2;
//
//            case IPF:
//                return CollectionType.BOC3;
//
//            case DOCSTAMP_FEE:
//                return CollectionType.BOC4;
//
//            default:
//                return null;
//        }
//    }

    public void setTransactionTypeCode(Boolean isCheck) {
        if (isCheck) {
            this.transactionTypeCode = TransactionTypeCode.CHK;
        } else {
            this.transactionTypeCode = TransactionTypeCode.CSH;
        }
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public void tagAsPaid() {
        this.setStatus(CDTStatus.PAID);
        this.datePaid = new Date();
    }

    public void setPaymentHistoryTotal(BigDecimal paymentHistoryTotal) {
        this.paymentHistoryTotal = paymentHistoryTotal;
    }

    public void tagAsPending() {
        this.setStatus(CDTStatus.PENDING);
    }

    public void tagAsNew() {
        this.setStatus(CDTStatus.NEW);
        this.datePaid = null;
        this.paymentReferenceNumber = null;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber;
    }

    public String getCifNumber() {
        return cifNumber;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public void tagAsRemitted() {
        this.isRemitted = Boolean.TRUE;
    }

    public Date getDateRemitted() {
		return dateRemitted;
	}

	public void setDateRemitted(Date dateRemitted) {
        this.dateRemitted = dateRemitted;
    }

    public Date getDateRefunded() {
		return dateRefunded;
	}

	public void setDateRefunded(Date dateRefunded) {
		this.dateRefunded = dateRefunded;
	}

	public Date getForRefundDate() {
		return forRefundDate;
	}

	public void setForRefundDate(Date forRefundDate) {
		this.forRefundDate = forRefundDate;
	}

	public void tagAsNotRemitted() {
        this.isRemitted = Boolean.FALSE;
    }

    public Boolean isRefunded() {
        if (CDTStatus.REFUNDED.equals(status)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public void updateDetails(Date pchcDateReceived, BigDecimal amount) {
        this.pchcDateReceived = pchcDateReceived;
        this.amount = amount;
    }

    public CollectionLine getCollectionLine() {
        return collectionLine;
    }

    public void setAllocationUnitCode(String allocationUnitCode) {
        this.allocationUnitCode = allocationUnitCode;
    }

	public String getBranchUnitCode() {
		return branchUnitCode;
	}

	public void setBranchUnitCode(String branchUnitCode) {
		this.branchUnitCode = branchUnitCode;
	}

//	public Date getPchcConfirmationDate() {
//		return pchcConfirmationDate;
//	}
//
//	public void setPchcConfirmationDate(Date pchcConfirmationDate) {
//		this.pchcConfirmationDate = pchcConfirmationDate;
//	}

	public String getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public String getOfficerCode() {
		return officerCode;
	}

	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}

	public Date getConfDate() {
		return confDate;
	}

	public void setConfDate(Date confDate) {
		this.confDate = confDate;
	}

	public Set<EmailNotif> getEmailNotifs() {
		return emailNotifs;
	}

	public void setEmailNotifs(Set<EmailNotif> emailNotifs) {
		this.emailNotifs = emailNotifs;
	}
	
//	public EmailNotif getEmailNotifs() {
//		return emailNotifs;
//	}
//
//	public void setEmailNotifs(EmailNotif emailNotifs) {
//		this.emailNotifs = emailNotifs;
//	}

//	public Set<EmailNotif> getEmailNotifs() {
//		return emailNotifs;
//	}
//
//	public void setEmailNotifs(EmailNotif emailNotif) {
//		if (this.emailNotifs != null) {
//			this.emailNotifs.add(emailNotif);
//		} else {
//			Set<EmailNotif>  emailNotifs2 = new HashSet<EmailNotif>();
//			emailNotifs2.add(emailNotif);
//			this.emailNotifs = emailNotifs2;
//		}
//	}

    public String getImportersEmail() {
        return importersEmail;
    }

    public String getRmbmEmail() {
        return rmbmEmail;
    }

    public String getBranchEmail() {
        return branchEmail;
    }

    public void setImportersEmail(String importersEmail) {
        this.importersEmail = importersEmail;
    }

    public void setRmbmEmail(String rmbmEmail) {
        this.rmbmEmail = rmbmEmail;
    }

    public void setBranchEmail(String branchEmail) {
        this.branchEmail = branchEmail;
    }
}
