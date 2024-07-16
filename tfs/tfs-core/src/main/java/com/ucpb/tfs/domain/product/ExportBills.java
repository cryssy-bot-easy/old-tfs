package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.condition.EnclosedInstruction;
import com.ucpb.tfs.domain.documents.DocumentsEnclosed;
import com.ucpb.tfs.domain.product.enums.*;
import com.ucpb.tfs.utils.UtilSetFields;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * (revision)
 *	SCR/ER Number:
 *	SCR/ER Description: 
 *		1. EBP Negotiation - Data Entry Inquiry (Redmine# 4152)
 *		2. Batch - Error in Executing Allocation File (Redmine# 4183)
 *	[Revised by:] Brian Harold A. Aquino
 *	[Date revised:] 
 *		1. 02/20/2017 (tfs Rev# 7258)
 *		2. 04/19/2017 (tfs Rev# 7314)
 *	[Date deployed:] 06/16/2017
 *	Program [Revision] Details: 
 *		1. Added new variable for Nego Advice Addressee and Nego Advice AddresseeAddress. 
 *		2. Added new variable for Negotiation Date.
 *	Member Type: Groovy
 *	Project: CORE
 *	Project Name: ExportBills.java
 */

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/1/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */

/**
(revision)
SCR/ER Number: 
SCR/ER Description: To save collectingBankCode/collectingBankAddress to ExportBills table.
[Revised by:] Jonh Henry Alabin
[Date deployed:] June 16,2017
Program [Revision] Details: Add getter/setter for collectingBankCode/collectingBankAddress.
Member Type: Java
Project: CORE
Project Name: ExportBills.java
 */

/**
 * 	(revision)
	Reference Number: ITDJCH-2018-03-001
	Task Description: Add new fields on screen of different modules to comply with the requirements of ITRS.
	[Created by:] Jaivee Hipolito
	[Date Revised:] 03/06/2018
	Program [Revision] Details: Add variable particulars including getter setter, to save particulars to Export Bills.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: ExportBills.java
 */

@Audited
public class ExportBills extends TradeProduct implements Serializable {

    private ExportBillType exportBillType;

    private Boolean exportViaPddtsFlag;
    private Boolean cwtFlag;

    private String paymentMode; // LC, Non-LC

    private BigDecimal outstandingAmount; // if from E/DBC, E/DBC Amount - amount, otherwise amount

	private DocumentNumber negotiationNumber; // if from E/DBC, documentNumber of E/DBC, otherwise null

    private LoanDetails loanDetails;

    private LcDetails lcDetails;

    private NonLcDetails nonLcDetails;

    // start data entry
    private String invoiceNumber;

    private BigDecimal additionalAmount; // additionalAmountClaimed

    private ChargesCode chargesCode;
    private BigDecimal chargeAmount;
    private String chargeNarrative;

    private String totalAmountClaimedFlag;
    private Date totalAmountClaimedDate;
    private BigDecimal totalAmountClaimed;
    private Currency totalAmountClaimedCurrency;

    private String corresBankFlag;
    private String corresBankCode;
    private String corresBankName;

    private String corresBankAccountFlag;
    private String corresBankAccountCode;
    private String corresBankAccountNameAndAddress;

    private AccountType accountType;

    private String depositoryAccountNumber;
    private String glCode;

    private Currency corresBankCurrency;

    private String senderToReceiverInformation;

    private Set<DocumentsEnclosed> documentsEnclosed;

    private Set<EnclosedInstruction> enclosedInstructions;

//    private String lastTransaction;

    private Date processDate;

    // settlement
    private Date settlementDate;
    private BigDecimal proceedsAmount;

    //collection
    private Boolean mtFlag;
    private String countryCode;

    private String sellerName;
    private String buyerName;
    private String buyerAddress;

    private String reasonForCancellation;

    private String partialNego;
    
    private String exporterCbCode;
    //added byt henry
    private String collectingbankcode;
    private String collectingbankaddress;
    //end
    private Currency bpCurrency;
    private BigDecimal bpAmount;
    
    //added by brian
    private String negoAdviceAddressee;
    private String negoAdviceAddresseeAddress;
    //end
    
    //added by Jaivee
    private String particulars;
    // end
    
    private Date negotiationDate;    

    public String getCollectingbankcode() {
		return collectingbankcode;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public void setCollectingbankcode(String collectingbankcode) {
		this.collectingbankcode = collectingbankcode;
	}

	public String getCollectingbankaddress() {
		return collectingbankaddress;
	}

	public void setCollectingbankaddress(String collectingbankaddress) {
		this.collectingbankaddress = collectingbankaddress;
	}

	public ExportBills() {}

    public ExportBills(DocumentNumber documentNumber, Map<String, Object> details, ProductType productType, ExportBillType exportBillType) {
        super(documentNumber, productType);

        super.setAmount(new BigDecimal((String) details.get("amount")));
        super.setCurrency(Currency.getInstance((String) details.get("currency")));

        super.updateStatus(TradeProductStatus.OPEN);

        this.exportBillType = exportBillType;
        this.outstandingAmount = new BigDecimal((String) details.get("amount"));

        this.documentsEnclosed = new HashSet<DocumentsEnclosed>();
        this.enclosedInstructions = new HashSet<EnclosedInstruction>();

        updateDetails(details);
    }

    public void updateDetails(Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);
    }

    @Override
    public void updateStatus(TradeProductStatus tradeProductStatus) {

        super.updateStatus(tradeProductStatus);

        switch(tradeProductStatus) {
            case OPEN:
                this.processDate = new Date();
                break;
            case CLOSED:
            	this.processDate = new Date();
        }
    }

    public void addDocumentsEnclosed(List<DocumentsEnclosed> documentsEnclosedList) {
        this.documentsEnclosed.clear();

        for(DocumentsEnclosed documentsEnclosed: documentsEnclosedList) {
            this.documentsEnclosed.add(documentsEnclosed);
        }
    }

    public void addEnclosedInstructions(List<EnclosedInstruction> enclosedInstructionList) {
        this.enclosedInstructions.clear();

        for(EnclosedInstruction enclosedInstruction: enclosedInstructionList) {
            this.enclosedInstructions.add(enclosedInstruction);
        }
    }

    public void setLcDetails(Map<String, Object> details) {
        LcDetails lcDetails = new LcDetails(details);

        this.lcDetails = lcDetails;
    }

    public void setNonLcDetails(Map<String, Object> details) {
        NonLcDetails nonLcDetails = new NonLcDetails(details);

        this.nonLcDetails = nonLcDetails;
    }
    
    public LcDetails getLcDetails() {
    	return lcDetails;
    }
    
    public NonLcDetails getNonLcDetails() {
    	return nonLcDetails;
    }

    public void setLoanDetails(Map<String, Object> details) {
        LoanDetails loanDetails = new LoanDetails(details);

        this.loanDetails = loanDetails;
    }

    public void settleExportBills(BigDecimal proceedsAmount, String partialNego) {
        this.updateStatus(TradeProductStatus.SETTLED);

        this.settlementDate = new Date();

        this.proceedsAmount = proceedsAmount;

        if (this.outstandingAmount != null){
        	this.outstandingAmount = this.outstandingAmount.subtract(this.proceedsAmount);
        }
        this.partialNego = partialNego;
    }

//    public void setLastTransaction(String lastTransaction) {
//        this.lastTransaction = lastTransaction;
//    }

    public DocumentNumber getDocumentNumber() {
        return super.getDocumentNumber();
    }

    public Set<DocumentsEnclosed> getDocumentsEnclosed() {
        return documentsEnclosed;
    }

    public Set<EnclosedInstruction> getEnclosedInstructions() {
        return enclosedInstructions;
    }

    public void setTotalAmountDetails(Date totalAmountClaimedDate, BigDecimal totalAmountClaimed, Currency totalAmountClaimedCurrency) {
        if (totalAmountClaimedDate != null) {
            this.totalAmountClaimedDate = totalAmountClaimedDate;
        }

        this.totalAmountClaimed = totalAmountClaimed;
        this.totalAmountClaimedCurrency = totalAmountClaimedCurrency;
    }

    public void cancelExportBills(String reasonForCancellation) {
        this.updateStatus(TradeProductStatus.CANCELLED);

        this.reasonForCancellation = reasonForCancellation;
        this.settlementDate = new Date();
    }

    public void setNegotiationNumber(DocumentNumber negotiationNumber) {
        this.negotiationNumber = negotiationNumber;
    }

    public ExportBillType getExportBillType() {
        return exportBillType;
    }
    
    public void setOutstandingAmount(BigDecimal outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		this.processDate = new Date(dateFormat.format(new Date()));
	}

    public BigDecimal getOutstandingAmount() {
		return outstandingAmount;
	}

	public Currency getBpCurrency() {
		return bpCurrency;
	}

	public BigDecimal getBpAmount() {
		return bpAmount;
	}

	public void setBpDetailsForCollection(Currency bpCurrency, BigDecimal bpAmount) {
		this.bpCurrency = bpCurrency;
		this.bpAmount = bpAmount;
	}
	
	public Date getNegotiationDate() {
		return negotiationDate;
	}
	
	// Added by Brian
	public void setNegotiationDate(Date negotiationDate) {
		this.negotiationDate = negotiationDate;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}
	
	public String getBuyerName() {
		return buyerName;
	}

	public String getBuyerAddress() {
		return buyerAddress;
	}
	// added by Henry
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

    public Date getProcessDate() {
        return processDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public AccountType getAccountType() {
        return accountType;
    }

	public String getNegoAdviceAddressee() {
		return negoAdviceAddressee;
	}

	public void setNegoAdviceAddressee(String negoAdviceAddressee) {
		this.negoAdviceAddressee = negoAdviceAddressee;
	}

	public String getNegoAdviceAddresseeAddress() {
		return negoAdviceAddresseeAddress;
	}

	public void setNegoAdviceAddresseeAddress(String negoAdviceAddresseeAddress) {
		this.negoAdviceAddresseeAddress = negoAdviceAddresseeAddress;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public ChargesCode getChargesCode() {
		return chargesCode;
	}

	public void setChargesCode(ChargesCode chargesCode) {
		this.chargesCode = chargesCode;
	}

	public String getChargeNarrative() {
		return chargeNarrative;
	}

	public void setChargeNarrative(String chargeNarrative) {
		this.chargeNarrative = chargeNarrative;
	}
}
