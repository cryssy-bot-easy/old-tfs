package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.condition.LcAdditionalCondition;
import com.ucpb.tfs.domain.documents.LcRequiredDocument;
import com.ucpb.tfs.domain.product.enums.*;
import com.ucpb.tfs.domain.reimbursing.LcInstructionToBank;
import com.ucpb.tfs.utils.UtilSetFields;
import org.hibernate.envers.Audited;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Jett
 * Date: 7/16/12
 */
 
 /**
	(revision)
	SCR/ER Number: SCR# IBD-15-1125-01
	SCR/ER Description: Added methods for Buyer Info
	[Revised by:] Jonh Henry Santos Alabin
	[Date revised:] 1/12/2017
	Program [Revision] Details: Added methods for Buyer Info
	Member Type: JAVA
	Project: CORE
	Project Name: LetterOfCredit.java
*/

/**
(revision)
SCR/ER Number: 
SCR/ER Description: 
[Revised by:] John Patrick C. Bautista
[Date revised:] July 27, 2017
[Date deployed:] 
Program [Revision] Details: Added new fields for Letter of Credit.
Member Type: Java
Project: tfs-core
Project Name: LetterOfCredit.java
 */

/**
(revision)
SCR/ER Number: 
SCR/ER Description: 
[Revised by:] Cedrick C. Nungay
[Date revised:] September 28, 2017
[Date deployed:] 
Program [Revision] Details: Added getTotalNegotiatedAmountResult and
							setCashAmount method for Letter of Credit.
Member Type: Java
Project: tfs-core
Project Name: LetterOfCredit.java
 */



/**
[Modified by:] Rafael T. Poblete
[Date Modified:] 8/28/2018
Details: Added swiftNarrativeRadio, otherPlaceOfExpiry, narrativeFor740 fields for SR 2018 requirement.
 */
 
/**
(revision)
SCR/ER Number: 
SCR/ER Description: 
[Revised by:] Cedrick C. Nungay
[Date revised:] August 8, 2018
[Date deployed:] 
Program [Revision] Details: Added purposeOfMessage, otherPlaceOfExpiry,
	specialPaymentConditionsForBeneficiary and specialPaymentConditionsForReceivingBank.
Member Type: Java
Project: tfs-core
Project Name: LetterOfCredit.java
 */

/**
(revision)
SCR/ER Number: 
SCR/ER Description: 
[Revised by:] Cedrick C. Nungay
[Date revised:] September 13, 2018
[Date deployed:] 
Program [Revision] Details: Added narrativeFor747.
Member Type: Java
Project: tfs-core
Project Name: LetterOfCredit.java
 */
 
@Audited
public class LetterOfCredit extends TradeProduct  {

    private LCDocumentType documentType;

    private Set<LCNegotiation> negotiations;

    private Set<LCNegotiationDiscrepancy> negotiationDiscrepancies;

    private String purpose;

    private Date processDate;
    private Date expiryDate;

    private String reasonForCancellation;
    private Date cancellationDate;

    private LCTenor tenor;

    private LCType type;

    private LCPaymentMode paymentMode;

    private Long usancePeriod;
    private String usancePeriodStart;

    private String expiryCountryCode;

    // flags
    private String partialShipment;
    private String partialDelivery;
    private String transShipment;
    private Boolean irrevocable;
    private Boolean negotiationRestriction;
    private Boolean adviseThroughBank;

    private LCPriceTerm priceTerm;

    // attributes for revolving LC
    private BigDecimal revolvingAmount;
    private LCRevolvingPeriod revolvingPeriod;
    private Integer daysRevolving;
    private Boolean cumulative;
    private BigDecimal aggregateAmount;

    private Boolean cashFlag;

    //todo: this can be calculated based on doc transactions instead (declare as transient?)
    private BigDecimal totalNegotiatedAmount;
    private Date lastNegotiationDate;

    private BigDecimal cashAmount;
    private BigDecimal totalNegotiatedCashAmount;

    private BigDecimal outstandingBalance;

    private BigDecimal refundAmount;

    // information related to shipment
    private String portOfOrigination;
    private String portOfDestination;
    private String portOfOriginCountryCode; // todo: this is a VO, declare a class?

    //todo: should we split this to multiple fields?
    private String importerAddress;
    private String beneficiaryAddress;

    private String beneficiaryName;

    //todo: these are value objects, should we declare a custom VO type?
    private String advisingBankCode;
    private String confirmingBankCode;

    private Currency reimbursingCurrency;

    private String drawee;

    private LCAdviseMedium adviseMedium;

    private Date latestShipmentDate;

    private String dispatchPlace;
    private String finalDestinationPlace;

    private String applicableRules;

    private String formOfDocumentaryCredit;

    private String destinationBank;

    private Date issueDate;

    private String priceTermNarrative;

    private String confirmationInstructionsFlag;

    private MarineInsurance marineInsurance;

    private String generalDescriptionOfGoods;

    private Boolean cwtFlag;

    private Boolean advanceCorresChargesFlag;

    private String otherPriceTerm;

    private String adviseThroughBankIdentifierCode;
    private String tenorOfDraftNarrative;
    private String maximumCreditAmount;
    private String shipmentPeriod;
    private String availableWithFlag;
    private String adviseThroughBankLocation;
    private String periodForPresentation;
    private Integer periodForPresentationNumber;
    private String periodForPresentationAdviseThroughBank;

    private String mixedPaymentDetails;
    private String importerName;
    private String placeOfFinalDestination;
    private String exporterName;
    private String placeOfTakingDispatchOrReceipt;
    private String exporterAddress;
    private BigDecimal negativeToleranceLimit;

    private String reimbursingBankFlag;
    private String adviseThroughBankNameAndAddress;
    private String identifierCode;
    private String availableBy;
    private String reimbursingBankNameAndAddress;
    private String senderToReceiverInformation;
    private String reimbursingBankIdentifierCode;
    private String nameAndAddress;

    private String reimbursingAccountType;
    private String importerCbCode;
    private String bspCountryCode;
    private String importerCifNumber;
    private String deferredPaymentDetails;
    private String reimbursingBankAccountNumber;
    private BigDecimal positiveToleranceLimit;
    private Date latestDateShipment;

    private String availableWith;
    private String additionalAmountsCovered;
    private String portOfDischargeOrDestination;
    private String adviseThroughBankFlag;
    private String senderToReceiverInformationNarrative;
    private String exporterCbCode;
    private String portOfLoadingOrDeparture;

    private String standbyTagging;
    private String furtherIdentification;
    private String purposeOfStandby;
    private String formatType;
    private String detailsOfGuarantee;

    private String applicantCifNumber;
    private String applicantName;
    private String applicantAddress;

    private String placeOfReceipt;
    private String placeOfDelivery;
    private String otherDocumentsInstructions;

    private Date dateClosed;

    private BigDecimal currentAmount;

    // for bgbe
    private Integer shipmentCount;

    // for amendment
    private Integer numberOfAmendments;

    private Date lastReinstatementDate;
    private Date lastAmendmentDate;

    private String lastTransaction;

    private Date lastModifiedDate;

    private String narrative;
    private String narrativeFor747;

    private String receiversReference;
    private String sendersReference;

    private String expiryPlace;

    private String swiftNarrativeRadio;

    private String otherPlaceOfExpiry;

    private String narrativeFor740;
    
    private String specialPaymentConditionsForBeneficiary;
    
    private String specialPaymentConditionsForReceivingBank;
    
    private String requestedConfirmationParty;

    // required documents
    private Set<LcRequiredDocument> requiredDocument;

    // instructions to the paying/accepting/negotiating bank
    private Set<LcInstructionToBank> instructionToBank;

    // additional conditions
    private Set<LcAdditionalCondition> additionalCondition;

    private boolean lastModifiedUpdated = false;

    private String purposeOfMessage;

    public boolean isLastModifiedUpdated() {
		return lastModifiedUpdated;
	}

	public void setLastModifiedUpdated(String serviceType) {
		this.lastModifiedUpdated = true;
        this.lastModifiedDate = new Date();
		
		if (serviceType.equalsIgnoreCase("NEGOTIATION")){
			this.lastNegotiationDate = this.lastModifiedDate;
		} else if (serviceType.equalsIgnoreCase("AMENDMENT")){
			this.lastAmendmentDate = this.lastModifiedDate;
		} else if (serviceType.equalsIgnoreCase("CANCELLATION")){
			this.cancellationDate = this.lastModifiedDate;
		}
	}

	public LetterOfCredit() {
        this.negotiations = new HashSet<LCNegotiation>();

        // required documents
        this.requiredDocument = new HashSet<LcRequiredDocument>();

        //instructions to the paying/accepting/negotiating bank
        this.instructionToBank = new HashSet<LcInstructionToBank>();

        //additional conditions
        this.additionalCondition = new HashSet<LcAdditionalCondition>();
        this.numberOfAmendments = 0;
    }

    public LetterOfCredit(DocumentNumber documentNumber, Map<String, Object> details) {

        super(documentNumber, ProductType.LC);

        this.updateDetails(details);

        this.currentAmount = this.amount;

        // this is added to set outstanding balance for new letter of credit
        this.outstandingBalance = this.amount;

        this.negotiations = new HashSet<LCNegotiation>();
        this.totalNegotiatedAmount = BigDecimal.ZERO;

        this.cashAmount = BigDecimal.ZERO;
        this.totalNegotiatedCashAmount = BigDecimal.ZERO;

        this.negotiationDiscrepancies = new HashSet<LCNegotiationDiscrepancy>();

        this.refundAmount = BigDecimal.ZERO;

        this.shipmentCount = 0;

        // required documents
        this.requiredDocument = new HashSet<LcRequiredDocument>();

        //instructions to the paying/accepting/negotiating bank
        this.instructionToBank = new HashSet<LcInstructionToBank>();

        //additional conditions
        this.additionalCondition = new HashSet<LcAdditionalCondition>();
        this.numberOfAmendments = 0;
    }

    //copy constructor
    public LetterOfCredit(LetterOfCredit letterOfCredit){
    	this.documentNumber = letterOfCredit.getDocumentNumber();
        this.documentType = letterOfCredit.getDocumentType();
        this.negotiations = letterOfCredit.getNegotiations();
        this.negotiationDiscrepancies = letterOfCredit.getNegotiationDiscrepancies();
        this.purpose = letterOfCredit.getPurpose();
        this.processDate = letterOfCredit.getProcessDate();
        this.expiryDate = letterOfCredit.getExpiryDate();
        this.reasonForCancellation = letterOfCredit.getReasonForCancellation();
        this.cancellationDate = letterOfCredit.getCancellationDate();
        this.tenor = letterOfCredit.getTenor();
        this.type = letterOfCredit.getType();
        this.paymentMode = letterOfCredit.getPaymentMode();
        this.usancePeriod = letterOfCredit.getUsancePeriod();
        this.usancePeriodStart = letterOfCredit.getUsancePeriodStart();
        this.expiryCountryCode = letterOfCredit.getExpiryCountryCode();
        this.partialShipment = letterOfCredit.getPartialShipment();
        this.partialDelivery = letterOfCredit.getPartialShipment();
        this.transShipment = letterOfCredit.getTransShipment();
        this.irrevocable = letterOfCredit.getIrrevocable();
        this.negotiationRestriction = letterOfCredit.getNegotiationRestriction();
        this.adviseThroughBank = letterOfCredit.getAdviseThroughBank();
        this.priceTerm = letterOfCredit.getPriceTerm();
        this.revolvingAmount = letterOfCredit.getRevolvingAmount();
        this.revolvingPeriod = letterOfCredit.getRevolvingPeriod();
        this.portOfOrigination = letterOfCredit.getPortOfOrigination();
        this.portOfDestination = letterOfCredit.getPortOfDestination();
        this.portOfOriginCountryCode = letterOfCredit.getPortOfOriginCountryCode(); // todo: this is a VO, declare a class?
        this.importerAddress = letterOfCredit.getImporterAddress();
        this.beneficiaryAddress = letterOfCredit.getBeneficiaryAddress();
        this.beneficiaryName = letterOfCredit.getBeneficiaryName();
        this.advisingBankCode = letterOfCredit.getAdvisingBankCode();
        this.confirmingBankCode = letterOfCredit.getConfirmingBankCode();
        this.drawee = letterOfCredit.getDrawee();
        this.dispatchPlace = letterOfCredit.getDrawee();
        this.finalDestinationPlace = letterOfCredit.getFinalDestinationPlace();
        this.exporterAddress = letterOfCredit.getExporterAddress();
        this.numberOfAmendments = 0;
        this.applicantAddress = letterOfCredit.getApplicantAddress();
        this.applicantName = letterOfCredit.getApplicantName();
    }

    @Override
    public void updateStatus(TradeProductStatus tradeProductStatus) {

        super.updateStatus(tradeProductStatus);

        switch(tradeProductStatus) {
            case OPEN:
                this.processDate = new Date();
            break;
        }
    }

    public void updateDetails(Map<String, Object> details) {
        System.out.println("\nLetterOfCredit.updateDetails() ===========\n");
        UtilSetFields.copyMapToObject(this, (HashMap<String,Object>)details);
    }

    // #2
    public void negotiate(String negotiationNumber, Map<String, Object> details) {
        // Create LC Negotiation
        LCNegotiation negotiation = new LCNegotiation(negotiationNumber);
        negotiation.updateDetails(details);
        negotiation.approveLcNegotiation();

        this.negotiations.add(negotiation);
        boolean withIC = false;
        // Close Negotiation Discrepancy, if there is any
        if ((String)details.get("icNumber") != null && !((String)details.get("icNumber")).equals("")) {
            ICNumber icNumber = new ICNumber((String)details.get("icNumber"));
            closeNegotiationDiscrepancy(icNumber);
            withIC = true;
        }

        // Execute negotiation
    	BigDecimal negotiationAmount = negotiation.getNegotiationAmount();
        if (!withIC) {
        	System.out.println("WITHOUT IC NUMBER");
        	

            // 1) Add current negotiated amount to the total negotiated amount.
            this.totalNegotiatedAmount = this.totalNegotiatedAmount.add(negotiationAmount);

            // 2) Do calculations involving AP Cash Amount.
            BigDecimal apCashAmount = this.getOutstandingApCashAmount();
            if (apCashAmount.compareTo(BigDecimal.ZERO) > 0) {
                if (apCashAmount.compareTo(negotiationAmount) >= 0) {
                    // Add negotiationAmount to totalNegotiatedCashAmount
                    this.totalNegotiatedCashAmount = this.totalNegotiatedCashAmount.add(negotiationAmount);
                    
                    // added by: arvin
                } else if (apCashAmount.compareTo(negotiationAmount) < 0) {
                	this.totalNegotiatedCashAmount = this.cashAmount;
                }
            }

            // 3) Finally, check outstanding balance.
            //    If outstanding balance = 0 or negative, set status of LC to CLOSED.
//            if (this.computeLcOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {

            //

            if (this.computeLcOutstandingBalance(negotiationAmount).compareTo(BigDecimal.ZERO) <= 0) {
                if (!this.status.equals(TradeProductStatus.EXPIRED)) {
                    this.status = TradeProductStatus.CLOSED;
                    this.dateClosed = new Date();

                    // this is added to update outstanding balance
                    if (this.computeLcOutstandingBalance(negotiationAmount).compareTo(BigDecimal.ZERO) <= 0) {
                        this.setOutstandingBalance(BigDecimal.ZERO);
                    } else {
                        this.setOutstandingBalance(this.computeLcOutstandingBalance(negotiationAmount));
                    }

                    resetTotalNegotiatedAmounts();
                }
                System.out.println("################# NEGOTIATION: LC status = " + this.status + "\n");
            } else {
                if (this.computeLcOutstandingBalance(negotiationAmount).compareTo(BigDecimal.ZERO) <= 0) {
                    this.setOutstandingBalance(BigDecimal.ZERO);
                } else {
                    this.setOutstandingBalance(this.computeLcOutstandingBalance(negotiationAmount));
                }
            }

        } else {
        	System.out.println("WITH IC NUMBER");
        	
        	validateForClosure();
        	
        }        
        
        lastNegotiationDate = new Date();

        if(this.shipmentCount == null) {
            this.shipmentCount = 0;
        }
        shipmentCount += 1;

        correctionOfStatus();
    }
    
    public void validateForClosure(){
    	if(this.outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {
    		if (!this.status.equals(TradeProductStatus.EXPIRED)) {
      			 this.status = TradeProductStatus.CLOSED;
                   this.dateClosed = new Date();
                   
                   resetTotalNegotiatedAmounts();
      		 }
      		 System.out.println("################# NEGOTIATION: LC status = " + this.status + "\n");
    	}  		     		
    }
    
    public void correctionOfStatus() {    	
    	System.out.println("LC Number : " + this.documentNumber + "    Status : " + this.status);
        if (this.status.equals(TradeProductStatus.CLOSED)) {        	
        	for(LCNegotiationDiscrepancy ic : this.negotiationDiscrepancies) {
        		System.out.println("IC Number : " + ic.getIcNumber() + "    Status : " + ic.getLcNegotiationDiscrepancyStatus());
        		if (ic.getLcNegotiationDiscrepancyStatus().equals(LCNegotiationDiscrepancyStatus.OPEN)) {
        			this.status = TradeProductStatus.OPEN;        	
        			break;
        		}
        	}
        	
        }        
    }

    private void resetTotalNegotiatedAmounts() {
        this.totalNegotiatedAmount = BigDecimal.ZERO;
        this.totalNegotiatedCashAmount = BigDecimal.ZERO;
    }

    public LCNegotiation getNegotiation(NegotiationNumber negotiationNumber) {

        LCNegotiation negotiation = null;
        Iterator<LCNegotiation> it = this.negotiations.iterator();
        while(it.hasNext()) {
            LCNegotiation nego =  it.next();
            if (nego.getNegotiationNumber().toString().equals(negotiationNumber.toString())) {
                negotiation = nego;
                break;
            }
        }

        return negotiation;
    }

    private Boolean closeNegotiationDiscrepancy(ICNumber icNumber) {

        Boolean success = Boolean.FALSE;

        LCNegotiationDiscrepancy lcNegotiationDiscrepancy = getNegotiationDiscrepancy(icNumber);
        if (lcNegotiationDiscrepancy != null) {
            lcNegotiationDiscrepancy.closeNegotiationDiscrepancy();
            success = Boolean.TRUE;
        }

        return success;
    }

    public LCNegotiationDiscrepancy getNegotiationDiscrepancy(ICNumber icNumber) {
        for(LCNegotiationDiscrepancy discrepancy : negotiationDiscrepancies){
            if(discrepancy.getIcNumber().equals(icNumber)){
                return discrepancy;
            }
        }
        return null;
    }

    public BigDecimal getTotalNegotiatedAmountResult() {
        return this.totalNegotiatedAmount;
    }

    public BigDecimal getTotalNegotiatedAmount() {

        BigDecimal total = BigDecimal.ZERO;
        Iterator<LCNegotiation> it = this.negotiations.iterator();
        while(it.hasNext()) {
            LCNegotiation negotiation =  it.next();
            total = total.add(negotiation.getNegotiationAmount());
        }

        return total;
    }

    public BigDecimal getTotalNegotiatedAmountOnThis() {
    	return this.totalNegotiatedAmount;
    }
    
    public void addNegotiationDiscrepancy(LCNegotiationDiscrepancy lcNegotiationDiscrepancy){
        this.negotiationDiscrepancies.add(lcNegotiationDiscrepancy);
    }

    public BigDecimal getOutstandingApCashAmount() {
        System.out.println("i am here 3");
        if (this.cashAmount == null) {
            System.out.println("i am here 4");
            return BigDecimal.ZERO;
        }
        System.out.println("i am here 5");
        return this.cashAmount.subtract(this.totalNegotiatedCashAmount);
    }

    // This is used to retrieve the computed Outstanding Balance from the database
    // This is called by the persistence mechanism
    public BigDecimal getLcOutstandingBalance() {

        BigDecimal subtrahend = this.getTotalNegotiatedAmount();
        BigDecimal outstandingBalance = this.currentAmount.subtract(subtrahend);

        System.out.println("\n#################### LC OUTSTANDING BALANCE = " + outstandingBalance.toPlainString() + "\n");

        // If computed outstanding balance is less than zero, return zero
        if (outstandingBalance.compareTo(BigDecimal.ZERO) > 0) {
            return outstandingBalance;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal computeLcOutstandingBalance() {

        BigDecimal subtrahend = this.getTotalNegotiatedAmount();
        BigDecimal outstandingBalance = this.retrieveOutstandingBalance().subtract(subtrahend);


        // If computed outstanding balance is less than zero, return zero
        if (outstandingBalance.compareTo(BigDecimal.ZERO) > 0) {
            return outstandingBalance;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal computeLcOutstandingBalance(BigDecimal negotiatedAmount) {
        System.out.println("#################################################");
        System.out.println("COMPUTING OUTSTANDING BALANCE");

        BigDecimal subtrahend = negotiatedAmount; //this.getTotalNegotiatedAmount();
        BigDecimal outstandingBalance = this.retrieveOutstandingBalance().subtract(subtrahend);

        System.out.println("NEGOTIATED AMOUNT : " + negotiatedAmount);
        System.out.println("RETRIEVE OUTSTANDING BALANCE : " + this.retrieveOutstandingBalance());
        System.out.println("OUTSTANDING BALANCE : " + outstandingBalance);
        System.out.println("#################################################");

        // If computed outstanding balance is less than zero, return zero
        if (outstandingBalance.compareTo(BigDecimal.ZERO) > 0) {
            return outstandingBalance;
        } else {
            return BigDecimal.ZERO;
        }
    }

    // This is used to store the computed Outstanding Balance into the database
    // This is called by the persistence mechanism
    public void setLcOutstandingBalance(BigDecimal lcOutstandingBalance) {

        BigDecimal subtrahend = this.getTotalNegotiatedAmount();

        if (this.outstandingBalance == null) {
            this.outstandingBalance = BigDecimal.ZERO;
        }

        if (this.currentAmount != null) {
            this.outstandingBalance = this.currentAmount.subtract(subtrahend);
        }
    }

    public void adjustAsCash(BigDecimal cashAmount) {
        // Set CASH LC attributes
        this.cashAmount = this.cashAmount.add(cashAmount);
        this.cashFlag = Boolean.TRUE;
    }

    public void setInitiallyAsCash() {
        // Set CASH LC attributes
        this.cashAmount = this.amount;
        this.cashFlag = Boolean.TRUE;
    }

//    public void amendLcAmount(BigDecimal newAmount) {
//        if (this.refundAmount == null) {  // For migrated LC's: they do were not created through the constructor
//            this.refundAmount = BigDecimal.ZERO;
//        }
//
//        if (newAmount.compareTo(this.currentAmount) < 0) { // decrease
//            this.refundAmount = this.refundAmount.add(newAmount.subtract(this.currentAmount));
//        } else if (newAmount.compareTo(this.currentAmount) > 0) { // increase
//            if (this.refundAmount.compareTo(newAmount) > 0) {
//                this.refundAmount = this.refundAmount.subtract(this.currentAmount.subtract(newAmount));
//            } else {
//                this.refundAmount = BigDecimal.ZERO;
//            }
//
//            this.outstandingBalance = this.refundAmount.subtract(this.getTotalNegotiatedAmount());
//        }
//
//        this.currentAmount = newAmount;
//
//        // Amends AP Cash Amount if Letter of Credit Type is CASH
//        if (this.type == LCType.CASH){
//        	this.cashAmount = this.currentAmount;
//        }
//
//        super.updateAmount(newAmount, this.currency);
//
//        if (this.status.equals(TradeProductStatus.CLOSED) && (this.outstandingBalance.compareTo(BigDecimal.ZERO) > 0)) {
//            this.status = TradeProductStatus.OPEN;
//        }
//    }


    // #1
    public void amendLcAmount(BigDecimal newAmount) {
        //this.increaseAmendmentCount();

        if (this.refundAmount == null) {  // For migrated LC's: they do were not created through the constructor
            this.refundAmount = BigDecimal.ZERO;
        }

        // If LC amount is being decreased, add difference to Refund Amount
        if (newAmount.compareTo(this.currentAmount) < 0) {

            this.refundAmount = this.refundAmount.add(newAmount.subtract(this.currentAmount));

        // If LC amount is increased, subtract difference from Refund Amount
        } else if (newAmount.compareTo(this.currentAmount) > 0) {

            if (this.refundAmount.compareTo(newAmount) > 0) {
                this.refundAmount = this.refundAmount.subtract(this.currentAmount.subtract(newAmount));
            } else {
                this.refundAmount = BigDecimal.ZERO;
            }

        }

        BigDecimal amountDifference = newAmount.subtract(this.currentAmount);
        BigDecimal outstandingBalance = this.retrieveOutstandingBalance(); //BigDecimal.ZERO;

        if ((this.numberOfAmendments != null && this.numberOfAmendments.compareTo(0) > 0) ||
                this.getTotalNegotiatedAmount().compareTo(BigDecimal.ZERO) > 0) {
            outstandingBalance = outstandingBalance.add(amountDifference);
        } else {
            outstandingBalance = newAmount;
        }

        this.setOutstandingBalance(outstandingBalance);

        this.currentAmount = newAmount;


        // Amends AP Cash Amount if Letter of Credit Type is CASH
        if (this.type == LCType.CASH){
        	this.cashAmount = this.currentAmount;
        }

        super.updateAmount(newAmount, this.currency);

        // "CLOSED LC will be changed to OPENED LC if there is INCREASE IN LC AMOUNT"
        System.out.println("LC OUTSTANDING BALANCE : " + this.retrieveOutstandingBalance());
        if (this.status.equals(TradeProductStatus.CLOSED) && (this.retrieveOutstandingBalance().compareTo(BigDecimal.ZERO) > 0)) {
            this.status = TradeProductStatus.OPEN;
        }

    }

    public void amendExpiryCountryCode(String expiryCountryCode) {
        this.expiryCountryCode = expiryCountryCode;
    }

    public void amendDestinationBank(String destinationBank) {
        this.destinationBank = destinationBank;
    }

    public void amendTenor(LCTenor lcTenor, Long usancePeriod) {
        this.tenor = lcTenor;
        if (tenor.equals(LCTenor.USANCE)) {
            this.usancePeriod = usancePeriod;
        }
    }

    public void amendApplicableRules(String applicableRules) {
        this.applicableRules = applicableRules;
    }

    public void amendConfirmationInstructionsFlag(String confirmationInstructionsFlag) {
        this.confirmationInstructionsFlag = confirmationInstructionsFlag;
    }

    public void amendExpiryDate(Date newExpiryDate) throws Exception {

        if (this.status.equals(TradeProductStatus.EXPIRED)) {

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date currentDate = new Date();
            currentDate = df.parse(df.format(currentDate));

            System.out.println("\nnewExpiryDate.compareTo(this.expiryDate) = " + newExpiryDate.compareTo(this.expiryDate));
            System.out.println("newExpiryDate.compareTo(currentDate) = " + newExpiryDate.compareTo(currentDate));
            System.out.println("this.retrieveOutstandingBalance().compareTo(BigDecimal.ZERO) = " + this.retrieveOutstandingBalance().compareTo(BigDecimal.ZERO) + "\n");

            // Assumes that all dates below are pure dates (i.e., 12/25/2012 00:00:00)
            if (((newExpiryDate.compareTo(this.expiryDate) > 0) && (newExpiryDate.compareTo(currentDate) > 0)) &&
                (this.retrieveOutstandingBalance().compareTo(BigDecimal.ZERO) > 0)) {

                // Make LC Open again
                this.status = TradeProductStatus.OPEN;
            }
        }

        this.expiryDate = newExpiryDate;
    }

    public void amendFormOfDocumentaryCredit(String formOfDocumentaryCredit) {
        this.formOfDocumentaryCredit = formOfDocumentaryCredit;
    }

    public void amendImporterCbCode(String importerCbCode) {
    	this.importerCbCode = importerCbCode;
    }
    public void amendImporterName(String importerName) {
        this.importerName = importerName;
    }
    public void amendImporterAddress(String importerAddress) {
        this.importerAddress = importerAddress;
    }
    public void amendExporterCbCode(String exporterCbCode) {
        this.exporterCbCode = exporterCbCode;
    }
    public void amendExporterName(String exporterName) {
        this.exporterName = exporterName;
    }
    public void amendExporterAddress(String exporterAddress) {
        this.exporterAddress = exporterAddress;
    }
    public void amendPositiveToleranceLimit(BigDecimal positiveToleranceLimit) {
        this.positiveToleranceLimit = positiveToleranceLimit;
    }
    public void amendNegativeToleranceLimit(BigDecimal negativeToleranceLimit) {
        this.negativeToleranceLimit = negativeToleranceLimit;
    }
    public void amendMaxCreditAmount(String maximumCreditAmount) {
        this.maximumCreditAmount = maximumCreditAmount;
    }
    public void amendAdditionalAmountsCovered(String additionalAmountsCovered) {
        this.additionalAmountsCovered = additionalAmountsCovered;
    }
    public void amendAvailableWith(String availableWith, String identifierCode, String nameAndAddress) {
        this.availableWith = availableWith;
        if (identifierCode != null && !identifierCode.equals("")) {
            this.identifierCode = identifierCode;
            this.nameAndAddress = null;
        } else if (nameAndAddress != null && !nameAndAddress.equals("")) {
            this.nameAndAddress = nameAndAddress;
            this.identifierCode = null;
        }
    }

    public void amendAvailableBy(String availableBy) {
        this.availableBy = availableBy;
    }
    public void amendDrawee(String drawee) {
        this.drawee = drawee;
    }
    public void amendTenorOfDraftNarrative(String tenorOfDraftNarrative) {
        this.tenorOfDraftNarrative = tenorOfDraftNarrative;
    }
    public void amendMixedPaymentDetails(String mixedPaymentDetails) {
        this.mixedPaymentDetails = mixedPaymentDetails;
    }
    public void amendDeferredPaymentDetails(String deferredPaymentDetails) {
        this.deferredPaymentDetails = deferredPaymentDetails;
    }
    public void amendPartialShipment(String partialShipment) {
        this.partialShipment = partialShipment;
    }
    public void amendTransShipment(String transShipment) {
        this.transShipment = transShipment;
    }
    public void amendPlaceDispatchReceipt(String placeOfTakingDispatchOrReceipt) {
        this.placeOfTakingDispatchOrReceipt = placeOfTakingDispatchOrReceipt;
    }
    public void amendPortLoadingDeparture(String portOfLoadingOrDeparture) {
        this.portOfLoadingOrDeparture = portOfLoadingOrDeparture;
    }
    public void amendBspCountryCode(String bspCountryCode) {
        this.bspCountryCode = bspCountryCode;
    }
    public void amendPortDischargeDestination(String portOfDischargeOrDestination) {
        this.portOfDischargeOrDestination = portOfDischargeOrDestination;
    }
    public void amendPlaceFinalDestination(String placeOfFinalDestination) {
        this.placeOfFinalDestination = placeOfFinalDestination;
    }

    public void amendLatestShipmentDate(Date latestShipmentDate) {
        this.latestShipmentDate = latestShipmentDate;
    }
    public void amendShipmentPeriod(String shipmentPeriod) {
        this.shipmentPeriod = shipmentPeriod;
    }
    public void amendGeneralDescriptionOfGoods(String generalDescriptionOfGoods) {
        this.generalDescriptionOfGoods = generalDescriptionOfGoods;
    }

    public String getGeneralDescriptionOfGoods() {
        return generalDescriptionOfGoods;
    }

    public void setGeneralDescriptionOfGoods(String generalDescriptionOfGoods) {
        this.generalDescriptionOfGoods = generalDescriptionOfGoods;
    }

    public void amendPeriodForPresentation(String periodForPresentation) {
        this.periodForPresentation = periodForPresentation;
    }
    
    public void amendPeriodForPresentation(String periodForPresentation, Integer periodForPresentationNumber) {
    	this.periodForPresentation = periodForPresentation;
    	this.periodForPresentationNumber = periodForPresentationNumber;
    }

    public void amendReimbursingBankDetails(String reimbursingBankFlag, String reimbursingBankIdentifierCode, String reimbursingBankNameAndAddress) {
    	this.reimbursingBankFlag = reimbursingBankFlag;
        if (reimbursingBankIdentifierCode != null && !reimbursingBankIdentifierCode.equals("")) {
            this.reimbursingBankIdentifierCode = reimbursingBankIdentifierCode;
            this.reimbursingBankNameAndAddress = null;
        } else if (reimbursingBankNameAndAddress != null && !reimbursingBankNameAndAddress.equals("")) {
            this.reimbursingBankNameAndAddress = reimbursingBankNameAndAddress;
            this.reimbursingBankIdentifierCode = null;
        }
    }
    public void amendReimbursingBankAccountType(String reimbursingAccountType) {
        this.reimbursingAccountType = reimbursingAccountType;
    }
    public void amendReimbursingCurrency(Currency reimbursingCurrency) {
        this.reimbursingCurrency = reimbursingCurrency;
    }
    public void amendReimbursingBankAccountNumber(String reimbursingBankAccountNumber) {
        this.reimbursingBankAccountNumber = reimbursingBankAccountNumber;
    }
    public void amendPeriodForPresentationAdviseThroughBank(String periodForPresentationAdviseThroughBank) {
        this.periodForPresentationAdviseThroughBank = periodForPresentationAdviseThroughBank;
    }
    public void amendAdviseThroughBankDetails(String adviseThroughBankFlag, String adviseThroughBankIdentifierCode, String adviseThroughBankLocation, String adviseThroughBankNameAndAddress) {
        this.adviseThroughBankFlag = adviseThroughBankFlag;
        if (adviseThroughBankIdentifierCode != null && !adviseThroughBankIdentifierCode.equals("")) {
            this.adviseThroughBankIdentifierCode = adviseThroughBankIdentifierCode;
            this.adviseThroughBankLocation = null;
            this.adviseThroughBankNameAndAddress = null;
        } else if (adviseThroughBankLocation != null && !adviseThroughBankLocation.equals("")) {
            this.adviseThroughBankLocation = adviseThroughBankLocation;
            this.adviseThroughBankIdentifierCode = null;
            this.adviseThroughBankNameAndAddress = null;
        } else if (adviseThroughBankNameAndAddress != null && !adviseThroughBankNameAndAddress.equals("")) {
            this.adviseThroughBankNameAndAddress = adviseThroughBankNameAndAddress;
            this.adviseThroughBankIdentifierCode = null;
            this.adviseThroughBankLocation = null;
        }
    }
    public void amendSenderToReceiverInformation(String senderToReceiver, String senderToReceiverInformation) {
        this.senderToReceiverInformation = senderToReceiver;
        this.senderToReceiverInformationNarrative = senderToReceiverInformation;
    }

    public void amendDetailsOfGuarantee(String detailsOfGuarantee) {
        this.detailsOfGuarantee = detailsOfGuarantee;
    }
    
    public void amendPartialDelivery(String partialDelivery) {
        this.partialDelivery = partialDelivery;
    }
    public void amendBeneficiaryName(String beneficiaryName) {
    	this.beneficiaryName = beneficiaryName;
    }
    public void amendBeneficiaryAddress(String beneficiaryAddress) {
    	this.beneficiaryAddress = beneficiaryAddress;
    }
    
    
    //added by henry Alabin
    public void amendApplicantName(String applicantName) {
    	this.applicantName = applicantName;
    }
    public void amendApplicantAddress(String applicantAddress) {
    	this.applicantAddress = applicantAddress;
    }
   
    public void amendRequiredDocuments(List<LcRequiredDocument> lcRequiredDocumentList){
    	//clears old entries of required documents
    	this.requiredDocument.clear();
    	addRequiredDocuments(lcRequiredDocumentList);
    }
    
    public void amendInstructionToBank(List<LcInstructionToBank> lcInstructionToBankList){
    	//clears old entries of instruction to bank
    	this.instructionToBank.clear();
    	addInstructionToBank(lcInstructionToBankList);
    }
    
    public void amendAdditionalCondition(List<LcAdditionalCondition> lcAdditionalConditionList){
    	//clears old entries of additional condition
    	this.additionalCondition.clear();
    	addAdditionalCondition(lcAdditionalConditionList);
    }

    public void amendPurposeOfMessage(String purposeOfMessage){
    	this.purposeOfMessage = purposeOfMessage;
    }
    
    public void amendOtherPlaceOfExpiry(String otherPlaceOfExpiry){
    	this.otherPlaceOfExpiry = otherPlaceOfExpiry;
    }

    public void cancelLc(String reasonForCancellation) {

        // Execute cancellation

        this.reasonForCancellation = reasonForCancellation;
        this.cancellationDate = new Date();
        this.status = TradeProductStatus.CANCELLED;
    }

    public Integer getShipmentCount() {
        return shipmentCount;
    }

    // avail indemnity through indemnity issuance
    public void availIndemnity() {
        if(this.shipmentCount == null) {
            this.shipmentCount = 0;
        }
        this.shipmentCount += 1;
    }

    // cancel indemnity
    public void cancelIndemnity() {
        if(this.shipmentCount == null) {
            this.shipmentCount = 0;
        }else {
            this.shipmentCount -= 1;
        }
    }

    // for amendment
    public void increaseAmendmentCount() {
        if(this.numberOfAmendments == null) {
            this.numberOfAmendments = 0;
        }
        this.numberOfAmendments += 1;
        this.lastAmendmentDate = new Date();
    }

    public void decreaseAmendmentCount() {
        if(this.numberOfAmendments == null) {
            this.numberOfAmendments = 0;
        }else {
            this.numberOfAmendments -= 1;
        }
    }

    public Integer getNumberOfAmendments() {
        return numberOfAmendments;
    }

    public void reinstate() {
        if (this.status.equals(TradeProductStatus.EXPIRED)) {
            this.status = TradeProductStatus.REINSTATED;
            this.lastReinstatementDate = new Date();
        }
    }

    public void negotiate() {
        if(this.shipmentCount == null) {
            this.shipmentCount = 0;
        }
        shipmentCount += 1;
    }

    public void updateLastTransaction(String lastTransaction) {
        this.lastTransaction = lastTransaction;
    }

    public void updateLastModifiedDate() {
        this.lastModifiedDate = new Date();
    }

    // required documents
    public void addRequiredDocuments(List<LcRequiredDocument> lcRequiredDocumentList) {
        for(LcRequiredDocument lcRequiredDocument : lcRequiredDocumentList) {
            this.requiredDocument.add(lcRequiredDocument);
        }
    }

    public void updateRequiredDocuments(Set<LcRequiredDocument> lcRequiredDocumentSet) {
        this.requiredDocument.clear();
        this.requiredDocument.addAll(lcRequiredDocumentSet);
    }

    public Set<LcRequiredDocument> getRequiredDocuments() {
        return requiredDocument;
    }

    // instructions to the paying/accepting/negotiating bank
    public void addInstructionToBank(List<LcInstructionToBank> lcInstructionToBankList) {
        for(LcInstructionToBank lcInstructionToBank: lcInstructionToBankList) {
            this.instructionToBank.add(lcInstructionToBank);
        }
    }

    public void updateInstructionToBank(Set<LcInstructionToBank> lcInstructionToBankSet) {
        this.instructionToBank.clear();
        this.instructionToBank.addAll(lcInstructionToBankSet);
    }

    public Set<LcInstructionToBank> getInstructionsToBank() {
        return instructionToBank;
    }

    // additional conditions
    public void addAdditionalCondition(List<LcAdditionalCondition> lcAdditionalConditionList) {
        for(LcAdditionalCondition lcAdditionalCondition: lcAdditionalConditionList) {
            this.additionalCondition.add(lcAdditionalCondition);
        }
    }

    public void updateAdditionalCondition(Set<LcAdditionalCondition> lcAdditionalConditionSet) {
        this.additionalCondition.clear();
        this.additionalCondition.addAll(lcAdditionalConditionSet);
    }

    public Set<LcAdditionalCondition> getAdditionalCondition() {
        return additionalCondition;
    }

    public void updatePurposeOfStandby(String purposeOfStandby) {
        this.purposeOfStandby = purposeOfStandby;
    }
    
    public void updateStandbyTagging(String standbyTagging) {
    	this.standbyTagging = standbyTagging;
    }

    public void updateNarrative(String narrative) {
        this.narrative = narrative;
    }

    public void updateNarrativeFor747(String narrativeFor747) {
        this.narrativeFor747 = narrativeFor747;
    }

    public String getReimbursingBankFlag() {
        return reimbursingBankFlag;
    }

    public String getAdviseThroughBankNameAndAddress() {
        return adviseThroughBankNameAndAddress;
    }

    public String getReimbursingBankNameAndAddress() {
        return reimbursingBankNameAndAddress;
    }

    public String getReimbursingBankIdentifierCode() {
        return reimbursingBankIdentifierCode;
    }

    public String getSenderToReceiverInformation() {
        return senderToReceiverInformation;
    }

    public String getReimbursingBankAccountNumber() {
        return reimbursingBankAccountNumber;
    }

    public String getAvailableWith() {
        return availableWith;
    }

    public String getAdditionalAmountsCovered() {
        return additionalAmountsCovered;
    }

    public String getPortOfDischargeOrDestination() {
        return portOfDischargeOrDestination;
    }

    public String getAdviseThroughBankFlag() {
        return adviseThroughBankFlag;
    }

    public String getSenderToReceiverInformationNarrative() {
        return senderToReceiverInformationNarrative;
    }

    public String getDestinationBank() {
        return destinationBank;
    }

    public String getFormOfDocumentaryCredit() {
        return formOfDocumentaryCredit;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public String getApplicableRules() {
        return applicableRules;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getExpiryCountryCode() {
        return expiryCountryCode;
    }

    public String getImporterName() {
        return importerName;
    }

    public Boolean getAdvanceCorresChargesFlag() {
        return advanceCorresChargesFlag;
    }

    public LCAdviseMedium getAdviseMedium() {
        return adviseMedium;
    }

    public Boolean getAdviseThroughBank() {
        return adviseThroughBank;
    }

    public String getAdviseThroughBankIdentifierCode() {
        return adviseThroughBankIdentifierCode;
    }

    public String getAdviseThroughBankLocation() {
        return adviseThroughBankLocation;
    }

    public String getAdvisingBankCode() {
        return advisingBankCode;
    }

    public BigDecimal getAggregateAmount() {
        return aggregateAmount;
    }

    public String getApplicantCifNumber() {
        return applicantCifNumber;
    }

    public String getApplicantAddress() {
        return applicantAddress;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getAvailableBy() {
        return availableBy;
    }

    public String getAvailableWithFlag() {
        return availableWithFlag;
    }

    public String getBeneficiaryAddress() {
        return beneficiaryAddress;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getBspCountryCode() {
        return bspCountryCode;
    }

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }


    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public Boolean getCashFlag() {
        return cashFlag;
    }
    
    public Boolean getCashFlagForNull() {
    	if (this.cashFlag == null) {
    		return false;
    	}
        return cashFlag;
    }
    
    public String getConfirmationInstructionsFlag() {
        return confirmationInstructionsFlag;
    }

    public String getConfirmingBankCode() {
        return confirmingBankCode;
    }

    public Boolean getCumulative() {
        return cumulative;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public Boolean getCwtFlag() {
        return cwtFlag;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public Integer getDaysRevolving() {
        return daysRevolving;
    }

    public String getDeferredPaymentDetails() {
        return deferredPaymentDetails;
    }

    public String getDetailsOfGuarantee() {
        return detailsOfGuarantee;
    }

    public String getDispatchPlace() {
        return dispatchPlace;
    }

    public LCDocumentType getDocumentType() {
        return documentType;
    }

    public String getDrawee() {
        return drawee;
    }

    public String getExporterAddress() {
        return exporterAddress;
    }

    public String getExporterCbCode() {
        return exporterCbCode;
    }

    public String getExporterName() {
        return exporterName;
    }

    public String getFinalDestinationPlace() {
        return finalDestinationPlace;
    }

    public String getFormatType() {
        return formatType;
    }

    public String getFurtherIdentification() {
        return furtherIdentification;
    }

    public String getIdentifierCode() {
        return identifierCode;
    }

    public String getImporterAddress() {
        return importerAddress;
    }

    public String getImporterCbCode() {
        return importerCbCode;
    }

    public String getImporterCifNumber() {
        return importerCifNumber;
    }

    public Set<LcInstructionToBank> getInstructionToBank() {
        return instructionToBank;
    }

    public Boolean getIrrevocable() {
        return irrevocable;
    }

    public Date getLastAmendmentDate() {
        return lastAmendmentDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Date getLastReinstatementDate() {
        return lastReinstatementDate;
    }

    public String getLastTransaction() {
        return lastTransaction;
    }

    public Date getLatestDateShipment() {
        return latestDateShipment;
    }

    public Date getLatestShipmentDate() {
        return latestShipmentDate;
    }

    public MarineInsurance getMarineInsurance() {
        return marineInsurance;
    }

    public String getMaximumCreditAmount() {
        return maximumCreditAmount;
    }

    public String getMixedPaymentDetails() {
        return mixedPaymentDetails;
    }

    public String getNameAndAddress() {
        return nameAndAddress;
    }

    public String getNarrative() {
        return narrative;
    }

    public BigDecimal getNegativeToleranceLimit() {
        return negativeToleranceLimit;
    }

    public Set<LCNegotiationDiscrepancy> getNegotiationDiscrepancies() {
        return negotiationDiscrepancies;
    }

    public Boolean getNegotiationRestriction() {
        return negotiationRestriction;
    }

    public Set<LCNegotiation> getNegotiations() {
        return negotiations;
    }

    public String getOtherDocumentsInstructions() {
        return otherDocumentsInstructions;
    }

    public String getOtherPriceTerm() {
        return otherPriceTerm;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public String getPartialDelivery() {
        return partialDelivery;
    }

    public String getPartialShipment() {
        return partialShipment;
    }

    public LCPaymentMode getPaymentMode() {
        return paymentMode;
    }

    public String getPeriodForPresentation() {
        return periodForPresentation;
    }

    public String getPeriodForPresentationAdviseThroughBank() {
        return periodForPresentationAdviseThroughBank;
    }

    public String getPlaceOfDelivery() {
        return placeOfDelivery;
    }

    public String getPlaceOfFinalDestination() {
        return placeOfFinalDestination;
    }

    public String getPlaceOfReceipt() {
        return placeOfReceipt;
    }

    public String getPlaceOfTakingDispatchOrReceipt() {
        return placeOfTakingDispatchOrReceipt;
    }

    public String getPortOfDestination() {
        return portOfDestination;
    }

    public String getPortOfLoadingOrDeparture() {
        return portOfLoadingOrDeparture;
    }

    public String getPortOfOrigination() {
        return portOfOrigination;
    }

    public String getPortOfOriginCountryCode() {
        return portOfOriginCountryCode;
    }

    public BigDecimal getPositiveToleranceLimit() {
        return positiveToleranceLimit;
    }

    public LCPriceTerm getPriceTerm() {
        return priceTerm;
    }

    public String getPriceTermNarrative() {
        return priceTermNarrative;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getPurposeOfStandby() {
        return purposeOfStandby;
    }

    public String getReasonForCancellation() {
        return reasonForCancellation;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public String getReimbursingAccountType() {
        return reimbursingAccountType;
    }

    public Currency getReimbursingCurrency() {
        return reimbursingCurrency;
    }

    public Set<LcRequiredDocument> getRequiredDocument() {
        return requiredDocument;
    }

    public BigDecimal getRevolvingAmount() {
        return revolvingAmount;
    }

    public LCRevolvingPeriod getRevolvingPeriod() {
        return revolvingPeriod;
    }

    public String getShipmentPeriod() {
        return shipmentPeriod;
    }

    public String getStandbyTagging() {
        return standbyTagging;
    }

    public LCTenor getTenor() {
        return tenor;
    }
    
    public void setTenor(LCTenor tenor) {
    	this.tenor = tenor;
    }

    public String getTenorOfDraftNarrative() {
        return tenorOfDraftNarrative;
    }

    public BigDecimal getTotalNegotiatedCashAmount() {
        return totalNegotiatedCashAmount;
    }
    
    public void setTotalNegotiatedCashAmount(BigDecimal totalNegotiatedCashAmount) {
		this.totalNegotiatedCashAmount = totalNegotiatedCashAmount;
	}

	public String getTransShipment() {
        return transShipment;
    }

    public LCType getType() {
        return type;
    }
    
    public void setType(LCType lcType) {
    	this.type = lcType;
    }

    public Long getUsancePeriod() {
        return usancePeriod;
    }

    public String getUsancePeriodStart() {
        return usancePeriodStart;
    }

    public Date getLastNegotiationDate() {
        return lastNegotiationDate;
    }

    // set the outstanding balance directly
    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public BigDecimal retrieveOutstandingBalance() {
        return outstandingBalance;
    }
    
    public void negotiateDiscrepancy(String icNumber, BigDecimal amount) {

        BigDecimal negotiationAmount = amount;

        this.totalNegotiatedAmount = this.totalNegotiatedAmount.add(negotiationAmount);

        BigDecimal apCashAmount = this.getOutstandingApCashAmount();
        if (apCashAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (apCashAmount.compareTo(negotiationAmount) >= 0) {
                this.totalNegotiatedCashAmount = this.totalNegotiatedCashAmount.add(negotiationAmount);
                
            } else if (apCashAmount.compareTo(negotiationAmount) < 0) {
            	this.totalNegotiatedCashAmount = this.cashAmount;
            }
        }

        if (this.computeLcOutstandingBalance(negotiationAmount).compareTo(BigDecimal.ZERO) <= 0) {
            if (!this.status.equals(TradeProductStatus.EXPIRED)) {

                // this is added to update outstanding balance
                if (this.computeLcOutstandingBalance(negotiationAmount).compareTo(BigDecimal.ZERO) <= 0) {
                    this.setOutstandingBalance(BigDecimal.ZERO);
                } else {
                    this.setOutstandingBalance(this.computeLcOutstandingBalance(negotiationAmount));
                }

            }
            System.out.println("################# NEGOTIATION: LC status = " + this.status + "\n");
        } else {
            if (this.computeLcOutstandingBalance(negotiationAmount).compareTo(BigDecimal.ZERO) <= 0) {
                this.setOutstandingBalance(BigDecimal.ZERO);
            } else {
                this.setOutstandingBalance(this.computeLcOutstandingBalance(negotiationAmount));
            }
        }


    }

    public void setTotalNegotiatedAmount(BigDecimal totalNegotiatedAmount) {
		this.totalNegotiatedAmount = totalNegotiatedAmount;
	}
    
    public void addTotalNegotiatedAmount(BigDecimal additional) {
		this.totalNegotiatedAmount = this.totalNegotiatedAmount.add(additional);
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public void setLastTransaction(String lastTransaction) {
		this.lastTransaction = lastTransaction;
	}
	
	public BigDecimal getOutstandingCashIcAmounts() {		
		BigDecimal sumOfIcAmount = BigDecimal.ZERO;
		
		for (LCNegotiationDiscrepancy ic : this.negotiationDiscrepancies) {
			
			if (ic.getLcNegotiationDiscrepancyStatus().equals(LCNegotiationDiscrepancyStatus.OPEN)) {
				sumOfIcAmount = sumOfIcAmount.add(ic.getCashAmount());
			}
			
		}		
		
		return sumOfIcAmount;		
	}
	
	public BigDecimal getOutstandingRegularIcAmounts() {		
		BigDecimal sumOfIcAmount = BigDecimal.ZERO;
		
		for (LCNegotiationDiscrepancy ic : this.negotiationDiscrepancies) {
			
			if (ic.getLcNegotiationDiscrepancyStatus().equals(LCNegotiationDiscrepancyStatus.OPEN)) {
				sumOfIcAmount = sumOfIcAmount.add(ic.getRegularAmount());
			}
			
		}		
		
		return sumOfIcAmount;		
	}

	public String getNarrativeFor740() {
		return narrativeFor740;
	}

	public void setNarrativeFor740(String narrativeFor740) {
		this.narrativeFor740 = narrativeFor740;
	}

	public String getRequestedConfirmationParty() {
		return requestedConfirmationParty;
	}

	public void setRequestedConfirmationParty(String requestedConfirmationParty) {
		this.requestedConfirmationParty = requestedConfirmationParty;
	}

    public void setPurposeOfMessage(String purposeOfMessage) {
        this.purposeOfMessage = purposeOfMessage;
    }

    public String getPurposeOfMessage() {
        return purposeOfMessage;		
    }

    public void setOtherPlaceOfExpiry(String otherPlaceOfExpiry) {
        this.otherPlaceOfExpiry = otherPlaceOfExpiry;
    }

    public String getOtherPlaceOfExpiry() {
        return otherPlaceOfExpiry;
    }

    public void setSpecialPaymentConditionsForBeneficiary(String specialPaymentConditionsForBeneficiary) {
        this.specialPaymentConditionsForBeneficiary = specialPaymentConditionsForBeneficiary;
    }

    public String getSpecialPaymentConditionsForBeneficiary() {
        return specialPaymentConditionsForBeneficiary;
    }

    public void setSpecialPaymentConditionsForReceivingBank(String specialPaymentConditionsForReceivingBank) {
        this.specialPaymentConditionsForReceivingBank = specialPaymentConditionsForReceivingBank;
    }

    public String getSpecialPaymentConditionsForReceivingBank() {
        return specialPaymentConditionsForReceivingBank;
    }

    public void setReceiversReference(String receiversReference) {
        this.receiversReference = receiversReference;
    }

    public String getReceiversReference() {
        return receiversReference;
    }
    
    public void amendAvailableWithNew(String availableWithFlag, String availableWith, String nameAndAddress) {   
    	if (availableWithFlag != null && !availableWithFlag.equals("")) {
    		this.availableWithFlag = availableWithFlag;
    	}
    	if (availableWith != null && !availableWith.equals("")) {
    		this.availableWith = availableWith;
    		this.nameAndAddress = null;
    	} else if (nameAndAddress != null && !nameAndAddress.equals("")) {
            this.nameAndAddress = nameAndAddress;
            this.availableWith = null;
    	}
    }
	
	
	public void printContent() throws IllegalArgumentException, IllegalAccessException{
		Field[] fields = this.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			if (field.get(this) != null) {
				System.out.println(field.getName() + " - " + field.get(this).toString().length() + " - " + field.get(this));
			} else {
				System.out.println(field.getName() + " - X - " + field.get(this));				
			}
		}
		
	}
}
