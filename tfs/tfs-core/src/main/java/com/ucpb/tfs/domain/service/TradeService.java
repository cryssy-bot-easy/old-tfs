package com.ucpb.tfs.domain.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.attach.Attachment;
import com.ucpb.tfs.domain.condition.AdditionalCondition;
import com.ucpb.tfs.domain.documents.RequiredDocument;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.letter.TransmittalLetter;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.IndemnityNumber;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reimbursing.InstructionToBank;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.domain.swift.SwiftCharge;
import com.ucpb.tfs.utils.DateUtil;
import com.ucpb.tfs.utils.UtilSetFields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang.SerializationUtils;
/**
 * User: Jett
 * Date: 7/12/12
 *
 * @author Jett Gamboa
 */


/**
	(revision)
SCR/ER Number: ER# 20151113-054
SCR/ER Description: No reinstatement entry for Reinstated LC thru amendment where amendment date = expiry date + 1.
[Revised by:] Jesse James Joson
[Date revised:] 11/17/2015
Program [Revision] Details: Aside from checking reinstateFlag column in DB, also include checking of expirydate, if expired and amended consider as reinstated.
PROJECT: CORE
MEMBER TYPE  : JAVA

*/

/**
(revision)
SCR/ER Number: ER# 20160517-099
SCR/ER Description: DW recon for April was imbalance, caused by LC 909-01-929-16-00098-4, which is amended on the day of the LC’s expiry date.
[Revised by:] Jesse James Joson
[Date revised:] 05/20/2016
Program [Revision] Details: Add checking if the expiry date = amendment date.
PROJECT: CORE
MEMBER TYPE  : JAVA
Project Name: TradeService.java
*/

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: TradeService
 */


@DomainAggregateRoot
public class TradeService implements Serializable {

    private TradeServiceId tradeServiceId;

    private ServiceInstructionId serviceInstructionId;

    private TradeServiceReferenceNumber tradeServiceReferenceNumber;

    private DocumentNumber documentNumber;

    private TradeProductNumber tradeProductNumber;

    // for bgbe
    private IndemnityNumber indemnityNumber;

    // reference to JBPM process instance
    private Long processId;

    // currency that charges will be paid in
    // from requirement that all charges are paid using one currency only
    private Currency chargesCurrency;

    private Set<ServiceCharge> serviceCharges;

    private ProductCharge productCharge;

    private TradeServiceStatus status;

    private UserActiveDirectoryId userActiveDirectoryId;

    // last user
    private UserActiveDirectoryId lastUser;

    private UserId createdBy;

    private Date createdDate;

    private Date modifiedDate;

    private String approvers;

    // cif number
    private String cifNumber;

    // cif name
    private String cifName;
    
    // cif full name
    private String longName;

    private String mainCifNumber;

    private String mainCifName;

    private String facilityId;

    private String facilityType;

    private String accountOfficer;

    // service type (Opening, Negotiation, Cancellation, etc)
    private ServiceType serviceType;

    // document type (Foreign, Domestic)
    private DocumentType documentType;

    // document class (LC, etc)
    private DocumentClass documentClass;

    // Regular, Cash, Standby
    private DocumentSubType1 documentSubType1;

    // Sight, Usance
    private DocumentSubType2 documentSubType2;

    // If Service Type is non-OPENING, put details here
    private Map<String, Object> details;


    // pass on rate
    private BigDecimal passOnRateThirdToUSD;
    private BigDecimal passOnRateUsdToPhp;
    private BigDecimal passOnRateThirdToPhp;
    private BigDecimal passOnUrr;

    // special rates
    private BigDecimal specialRateThirdToUsd;
    private BigDecimal specialRateUsdToPhp;
    private BigDecimal specialRateThirdToPhp;
    private BigDecimal specialRateUrr;

    // pass on rate for serviceCharge
    private BigDecimal passOnRateThirdToUSDServiceCharge;
    private BigDecimal passOnRateUsdToPhpServiceCharge;
    private BigDecimal passOnRateThirdToPhpServiceCharge;
    private BigDecimal passOnUrrServiceCharge;

    // special rates for serviceCharge
    private BigDecimal specialRateThirdToUsdServiceCharge;
    private BigDecimal specialRateUsdToPhpServiceCharge;
    private BigDecimal specialRateThirdToPhpServiceCharge;
    private BigDecimal specialRateUrrServiceCharge;



    private String narrative;

    private String reasonForCancellation;

    private String processingUnitCode;

    private String ccbdBranchUnitCode;

    private String allocationUnitCode;

    private Set<Attachment> attachments;

    private PaymentStatus paymentStatus;

    private Boolean reinstateFlag;

    // required documents
    private Set<RequiredDocument> requiredDocument;

    // instructions to the paying/accepting/negotiating bank
    private Set<InstructionToBank> instructionToBank;
    
    // additional conditions
    private Set<AdditionalCondition> additionalCondition;

    // transmittal letter
    private Set<TransmittalLetter> transmittalLetter;

    // swift charge
    private Set<SwiftCharge> swiftCharge;

    // Product Refund details
    private Set<ProductRefundDetail> productRefundDetails;

    // Product Collectible details
    private Set<ProductCollectibleDetail> productCollectibleDetails;

    // Other Charge details
    private Set<OtherChargesDetail> otherChargesDetails;

    // prepared by
    UserActiveDirectoryId preparedBy;

    private Boolean icDiscrepancy;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private String tinNumber;
    
    private String officerCode;
    private String exceptionCode;
    
    private Date maturityDate;
    
    private Date expiryDate;
    private Date processDate;

	public TradeService() {

        this.tradeServiceId = new TradeServiceId();
        this.details = new HashMap<String, Object>();
        this.createdDate = new Date();
        this.modifiedDate = this.createdDate;
        this.attachments = new HashSet<Attachment>();
        this.paymentStatus = PaymentStatus.UNPAID;

        // required documents
        this.requiredDocument = new HashSet<RequiredDocument>();
        
        // instruction to bank
        this.instructionToBank = new HashSet<InstructionToBank>();

        // additional conditions
        this.additionalCondition = new HashSet<AdditionalCondition>();

        // transmittal letter
        this.transmittalLetter = new HashSet<TransmittalLetter>();

        // swift charge
        this.swiftCharge = new HashSet<SwiftCharge>();

        // Product Refund details
        this.productRefundDetails = new HashSet<ProductRefundDetail>();

        // Product Collectible details
        this.productCollectibleDetails = new HashSet<ProductCollectibleDetail>();

        // Other Charge details
        this.otherChargesDetails = new HashSet<OtherChargesDetail>();
    }

    // Use this if TradeService HAS NO ETS
    // This generates TradeServiceReferenceNumber
    public TradeService(DocumentNumber documentNumber, TradeProductNumber tradeProductNumber, DocumentClass documentClass, DocumentType documentType, DocumentSubType1 documentSubType1, DocumentSubType2 documentSubType2, ServiceType serviceType, UserActiveDirectoryId userActiveDirectoryId, String tradeServiceReferenceNumber) {

        this(null, documentNumber, tradeProductNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType, userActiveDirectoryId);

        this.tradeServiceReferenceNumber = new TradeServiceReferenceNumber(tradeServiceReferenceNumber);
        this.approvers="";

        // required documents
        this.requiredDocument = new HashSet<RequiredDocument>();

        // instruction to bank
        this.instructionToBank = new HashSet<InstructionToBank>();

        // additional conditions
        this.additionalCondition = new HashSet<AdditionalCondition>();

        // transmittal letter
        this.transmittalLetter = new HashSet<TransmittalLetter>();

        // swift charge
        this.swiftCharge = new HashSet<SwiftCharge>();
    }

    // Use this if TradeService HAS ETS
    public TradeService(ServiceInstructionId serviceInstructionId, DocumentNumber documentNumber, TradeProductNumber tradeProductNumber, DocumentClass documentClass, DocumentType documentType, DocumentSubType1 documentSubType1, DocumentSubType2 documentSubType2, ServiceType serviceType, UserActiveDirectoryId userActiveDirectoryId) {

        this();
        this.approvers="";

        if (serviceInstructionId != null) {
            this.serviceInstructionId = serviceInstructionId;
        }

        if (documentNumber != null) {
            this.documentNumber = documentNumber;
        }

        if (tradeProductNumber != null) {
            this.tradeProductNumber = tradeProductNumber;
        }

        if (documentClass != null) {
            this.documentClass = documentClass;
        }
        if (documentType != null) {
            this.documentType = documentType;
        }
        if (documentSubType1 != null) {
            this.documentSubType1 = documentSubType1;
        }
        if (documentSubType2 != null) {
            this.documentSubType2 = documentSubType2;
        }
        if (serviceType != null) {
            this.serviceType = serviceType;
        }

        this.userActiveDirectoryId = userActiveDirectoryId;
        this.createdBy = new UserId(userActiveDirectoryId.toString());

        this.attachments = new HashSet<Attachment>();

        //TODO Copy Attachments from ETS

        // required documents
        this.requiredDocument = new HashSet<RequiredDocument>();

        // instruction to bank
        this.instructionToBank = new HashSet<InstructionToBank>();

        // additional conditions
        this.additionalCondition = new HashSet<AdditionalCondition>();

        // transmittal letter
        this.transmittalLetter = new HashSet<TransmittalLetter>();

        // swift charge
        this.swiftCharge = new HashSet<SwiftCharge>();
    }

    public void updateDetails(Map<String, Object> details, UserActiveDirectoryId userActiveDirectoryId) {
        System.out.println("updateDetails");
        this.details.putAll(details);

        UtilSetFields.copyMapToObject(this, (HashMap) this.details);

        if(userActiveDirectoryId != null) {
            this.userActiveDirectoryId = userActiveDirectoryId;
        }
        this.modifiedDate = new Date();
    }

    public void updateDetails(Map<String, Object> details) {

        this.details.putAll(details);

        UtilSetFields.copyMapToObject(this, (HashMap) this.details);
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public void updateProductCharge(Map<String, Object> details, UserActiveDirectoryId userActiveDirectoryId) {
    	System.out.println("went to updateProductCharge");
        ServiceType serviceType = ServiceType.valueOf(((String) details.get("serviceType")).toUpperCase());

        DocumentClass documentClass = DocumentClass.valueOf(((String) details.get("documentClass")).toUpperCase());
        ProductCharge productCharge = null;
        System.out.println("documentClass = " + documentClass);
        //	For Import Products
        
        //		for non-lcs
        if (documentClass.equals(DocumentClass.DA) || documentClass.equals(DocumentClass.DP) || documentClass.equals(DocumentClass.OA) || documentClass.equals(DocumentClass.DR)){
            if (details.get("amount") != null && details.get("currency") != null) {
                System.out.println("currency:" + details.get("currency").toString() + ":");
                BigDecimal amount = BigDecimal.ZERO;
                	if (serviceType.equals(ServiceType.NEGOTIATION) || serviceType.equals(ServiceType.NEGOTIATION_ACCEPTANCE)){
                		amount = new BigDecimal(details.get("amount").toString());
                	} else if (serviceType.equals(ServiceType.SETTLEMENT)){
                		amount = new BigDecimal(details.get("productAmount").toString());
                	}
                Currency currency = Currency.getInstance((String) details.get("currency").toString().trim());
                productCharge = new ProductCharge(amount, currency);
            }
        }
        //		for lcs
        else {
        switch (serviceType) {

            case ISSUANCE:
                if (details.get("shipmentAmount") != null && details.get("shipmentCurrency") != null) {
                    BigDecimal amount = new BigDecimal((String) details.get("shipmentAmount"));
                    Currency currency = Currency.getInstance((String) details.get("shipmentCurrency"));
                    productCharge = new ProductCharge(amount, currency);
                }
                break;

            case CANCELLATION:
//                DocumentClass documentClass = DocumentClass.valueOf(((String) details.get("documentClass")).toUpperCase());
                // cancellation  of bgbe
                if (documentClass == DocumentClass.INDEMNITY) {
                    BigDecimal amount = new BigDecimal((String) details.get("shipmentAmount"));
                    Currency currency = Currency.getInstance((String) details.get("shipmentCurrency"));
                    productCharge = new ProductCharge(amount, currency);
                }
                break;

            case NEGOTIATION:

                if (details.get("negotiationAmount") != null && details.get("negotiationCurrency") != null) {

                    BigDecimal negotiationAmount = new BigDecimal((String) details.get("negotiationAmount"));
                    Currency currency = Currency.getInstance((String) details.get("negotiationCurrency"));

                    Boolean cashFlag = null;
                    BigDecimal cashAmount = null;
                    if ((details.get("cashFlag") != null && !details.get("cashFlag").equals("")) && (details.get("cashAmount") != null && !details.get("cashAmount").equals(""))) {
                        cashFlag = Boolean.valueOf((String) details.get("cashFlag"));
                        cashAmount = new BigDecimal((String) details.get("cashAmount"));
                    }

                    BigDecimal outstandingBalance = new BigDecimal((String) details.get("outstandingBalance"));

                    BigDecimal amount = BigDecimal.ZERO;

                    if ((cashFlag != null && cashFlag) && cashAmount != null) {
                        if (negotiationAmount.compareTo(cashAmount) > 0) {
                            amount = negotiationAmount.subtract(cashAmount);
                        } else {
                            amount = negotiationAmount;
                        }
                    } else {
                        if (negotiationAmount.compareTo(outstandingBalance) > 0) {
                            amount = negotiationAmount.subtract(outstandingBalance);
                        } else {
                            amount = negotiationAmount;
                        }
                    }

                    productCharge = new ProductCharge(amount, currency);
                }
                break;

            case NEGOTIATION_DISCREPANCY:

                String negotiationAmountStr = (String)details.get("negotiationAmount");
                String negotiationCurrencyStr = (String)details.get("negotiationCurrency");

                if ((negotiationAmountStr != null && !negotiationAmountStr.equals("")) &&
                    (negotiationCurrencyStr != null && !negotiationCurrencyStr.equals(""))) {

                    BigDecimal negotiationAmount = new BigDecimal(negotiationAmountStr);
                    Currency currency = Currency.getInstance(negotiationCurrencyStr);

                    productCharge = new ProductCharge(negotiationAmount, currency);
                }
                break;

            case ADJUSTMENT:
                String partialCashSettlementFlag = (String) details.get("partialCashSettlementFlag");
                if (partialCashSettlementFlag != null && partialCashSettlementFlag.equals("partialCashSettlementEnabled")) {
                    if (details.get("cashAmount") != null && details.get("currency") != null) {
                        BigDecimal amount = new BigDecimal((String) details.get("cashAmount"));
                        Currency currency = Currency.getInstance((String) details.get("currency"));
                        productCharge = new ProductCharge(amount, currency);
                    }
                }
                break;

            case AMENDMENT:

                DocumentSubType1 documentSubType1 = DocumentSubType1.valueOf(((String)details.get("documentSubType1")).toUpperCase());

                if (documentSubType1.equals(DocumentSubType1.CASH)) {

                    if (details.get("amountSwitch") != null && (!((String)details.get("amountSwitch")).equals("")) && (!((String)details.get("amountSwitch")).toLowerCase().equals("off"))) {

                        if (details.get("amountTo") != null && (!(details.get("amountTo").toString()).equals(""))) {

                            BigDecimal outstandingBalance = null;

                            if (((String)details.get("referenceType")).equals("ETS")) {
                                if (details.get("outstandingBalance") != null && !(details.get("outstandingBalance").toString()).equals("")) {
                                    outstandingBalance = new BigDecimal(details.get("outstandingBalance").toString());
                                }
                            } else if (((String)details.get("referenceType")).equals("DATA_ENTRY")) {
                                if (details.get("amountFrom") != null && !(details.get("amountFrom").toString()).equals("")) {
                                    outstandingBalance = new BigDecimal(details.get("amountFrom").toString());
                                }
                            }

                            BigDecimal amountTo = new BigDecimal(details.get("amountTo").toString());

                            // Increase in LC amount
                            if (amountTo.compareTo(outstandingBalance) > 0) {
                                BigDecimal difference = amountTo.subtract(outstandingBalance);
                                Currency currency = Currency.getInstance((String) details.get("currency"));
                                productCharge = new ProductCharge(difference, currency);
                            }
                        }
                    } else {
                        Currency currency = Currency.getInstance((String) details.get("currency"));
                        BigDecimal amount = new BigDecimal(details.get("amount").toString());
                        productCharge = new ProductCharge(amount, currency);
                    }
                } else {
                    if (details.get("amountSwitch") != null && (!((String)details.get("amountSwitch")).equals("")) && (!((String)details.get("amountSwitch")).toLowerCase().equals("off"))) {

                        if (details.get("amountTo") != null && (!(details.get("amountTo").toString()).equals(""))) {

                            BigDecimal outstandingBalance = null;

                            if (((String)details.get("referenceType")).equals("ETS")) {
                                if (details.get("outstandingBalance") != null && !(details.get("outstandingBalance").toString()).equals("")) {
                                    outstandingBalance = new BigDecimal(details.get("outstandingBalance").toString());
                                }
                            } else if (((String)details.get("referenceType")).equals("DATA_ENTRY")) {
                                if (details.get("amountFrom") != null && !(details.get("amountFrom").toString()).equals("")) {
                                    outstandingBalance = new BigDecimal(details.get("amountFrom").toString());
                                }
                            }

                            BigDecimal amountTo = new BigDecimal(details.get("amountTo").toString());

                            // Increase in LC amount
                            if (amountTo.compareTo(outstandingBalance) > 0) {
                                BigDecimal difference = amountTo.subtract(outstandingBalance);
                                Currency currency = Currency.getInstance((String) details.get("currency"));
                                productCharge = new ProductCharge(difference, currency);
                            }
                        }
                    } else {
                        Currency currency = Currency.getInstance((String) details.get("currency"));
                        BigDecimal amount = new BigDecimal(details.get("amount").toString());
                        productCharge = new ProductCharge(amount, currency);

                    }

                }


                break;

            case OPENING:
                if (details.get("amount") != null && details.get("currency") != null) {
                    System.out.println("currency:" + details.get("currency").toString() + ":");
                    BigDecimal amount = new BigDecimal(details.get("amount").toString());
                    Currency currency = Currency.getInstance((String) details.get("currency").toString().trim());
                    productCharge = new ProductCharge(amount, currency);
                }
                break;

            case UA_LOAN_SETTLEMENT:
                if (details.get("amount") != null && details.get("currency") != null) {
                    BigDecimal amount = new BigDecimal((String) details.get("amount"));
                    Currency currency = Currency.getInstance((String) details.get("currency"));
                    productCharge = new ProductCharge(amount, currency);
                }
                break;

            case PAYMENT:
                if(details.get("documentClass").toString().equalsIgnoreCase("CDT")) {
                    if (details.get("totalAmountDue") != null) {
                        BigDecimal amount = new BigDecimal(details.get("totalAmountDue").toString().replaceAll(",", ""));

                        // CDT is always in PHP
                        Currency currency = Currency.getInstance("PHP");
                        productCharge = new ProductCharge(amount, currency);
                    }
                }
                break;
            case REFUND:
                if(details.get("documentClass").toString().equalsIgnoreCase("CDT")) {
                    if (details.get("totalAmountOfPayment") != null) {
                        BigDecimal amount = new BigDecimal(details.get("totalAmountOfPayment").toString().replaceAll(",", ""));
                        // CDT is always in PHP
                        Currency currency = Currency.getInstance("PHP");
                        productCharge = new ProductCharge(amount, currency);
                    }
                }
                break;
            default:
        }
        }

        if (productCharge != null) {
            this.productCharge = productCharge;
        } else {
            this.productCharge = null;
        }

        this.userActiveDirectoryId = userActiveDirectoryId;
        this.modifiedDate = new Date();
        System.out.println("\nProductCharge was updated!\n");
    }

    // this is used to store details as JSON into our database
    // this is not meant to be called by anything other than by the persistence mechanism
    public void setTradeServiceDetails(String tradeServiceDetails) {

        Gson gson = new Gson();

        // use GSON to deserialize from JSON to our HashMap
        details = gson.fromJson(tradeServiceDetails, new TypeToken<HashMap<String, String>>() {
        }.getType());
    }

    // this is used to retrieve details stored as JSON in the database to our HashMap
    // this is not meant to be called by anything other than by the persistence mechanism
    public String getTradeServiceDetails() {

        Gson gson = new Gson();

        // use GSON to serialize our HashMap to a JSON string that will be stored in the DB
        return gson.toJson(details);
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void Service(DocumentNumber documentNumber) {
        this.documentNumber = documentNumber;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public void addCharge(ChargeId chargeId, BigDecimal amount, Currency currency, UserActiveDirectoryId userActiveDirectoryId) {

        // todo: remove this
        addCharge(chargeId, amount, currency);

        this.userActiveDirectoryId = userActiveDirectoryId;
        this.modifiedDate = new Date();
    }

    public void addCharge(ChargeId chargeId, BigDecimal amount, Currency currency, UserActiveDirectoryId userActiveDirectoryId, BigDecimal originalAmount, Currency originalCurrency) {
        System.out.println("addCharge addCharge");

        addCharge(chargeId, amount, currency, originalAmount, originalCurrency);

        this.userActiveDirectoryId = userActiveDirectoryId;
        this.modifiedDate = new Date();
    }


    public void addCharge(ChargeId chargeId, BigDecimal amount, Currency currency, UserActiveDirectoryId userActiveDirectoryId, BigDecimal originalAmount, Currency originalCurrency, BigDecimal defaultAmount, String overriddenFlag, BigDecimal nocwtAmount) {
        System.out.println("addCharge addCharge");

        addCharge(chargeId, amount, currency, originalAmount, originalCurrency, defaultAmount, overriddenFlag, nocwtAmount);

        this.userActiveDirectoryId = userActiveDirectoryId;
        this.modifiedDate = new Date();
    }

    public void removeServiceCharges() {

        System.out.println("Clearing Service Charges");
        if (this.serviceCharges != null) {
            this.serviceCharges.clear();
        }
    }

    public void addCharge(ChargeId chargeId, BigDecimal amount, Currency currency) {
        System.out.println("add charge old");
        // create our service charge
        ServiceCharge serviceCharge = new ServiceCharge(this.tradeServiceId, chargeId, amount, currency);

        // create set if it does not exist already
        if (serviceCharges == null) {
            serviceCharges = new HashSet<ServiceCharge>();
        }

        // add service charge
        serviceCharges.add(serviceCharge);
    }

    public void addCharge(ChargeId chargeId, BigDecimal amount, Currency currency, BigDecimal originalAmount, Currency originalCurrency) {
        System.out.println("add charge new");
        // create our service charge
        ServiceCharge serviceCharge = new ServiceCharge(this.tradeServiceId, chargeId, amount, currency, originalAmount, originalCurrency);

        // create set if it does not exist already
        if (serviceCharges == null) {
            serviceCharges = new HashSet<ServiceCharge>();
        }

        // add service charge
        serviceCharges.add(serviceCharge);
    }

    public void addCharge(ChargeId chargeId, BigDecimal amount, Currency currency, BigDecimal originalAmount, Currency originalCurrency, BigDecimal defaultAmount, String overriddenFlag, BigDecimal nocwtAmount) {
        System.out.println("add charge new");
        // create our service charge
        ServiceCharge serviceCharge = new ServiceCharge(this.tradeServiceId, chargeId, amount, currency, originalAmount, originalCurrency, defaultAmount, overriddenFlag, nocwtAmount);

        // create set if it does not exist already
        if (serviceCharges == null) {
            serviceCharges = new HashSet<ServiceCharge>();
        }

        // add service charge
        serviceCharges.add(serviceCharge);
    }

    public void updateCharge(ChargeId chargeId, BigDecimal amount, Currency currency) {

        Iterator it = serviceCharges.iterator();

        // iterate through all existing charges
        while (it.hasNext()) {
            ServiceCharge sc = (ServiceCharge) it.next();

            // if an item matches, update it
            if (sc.matches(chargeId)) {
                sc.update(amount, currency);
            }
        }
    }

    public void updateCharge(ChargeId chargeId, BigDecimal amount, Currency currency, BigDecimal originalAmount, Currency originalCurrency) {

        Iterator it = serviceCharges.iterator();

        // iterate through all existing charges
        while (it.hasNext()) {
            ServiceCharge sc = (ServiceCharge) it.next();

            // if an item matches, update it
            if (sc.matches(chargeId)) {
                sc.update(amount, currency, originalAmount, originalCurrency);
            }
        }
    }

    public void setProductCharge(BigDecimal amount, Currency currency) {
        ProductCharge productCharge = new ProductCharge(amount, currency);
        this.productCharge = productCharge;
    }

    public void tagStatus(TradeServiceStatus status) {
        this.status = status;
        this.modifiedDate = new Date();
    }

    public void updateStatus(TradeServiceStatus status, UserActiveDirectoryId userActiveDirectoryId) {
        tagStatus(status);
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public void updateStatus(TradeServiceStatus status, UserId userId) {

        System.out.println("updating status to : " + status.toString() + " for " + userId.toString());

        tagStatus(status);

        //this.userActiveDirectoryId = userActiveDirectoryId;

        // increment approval count if this was tagged as checked or approved
        if (status.equals(TradeServiceStatus.CHECKED) || status.equals(TradeServiceStatus.APPROVED) || status.equals(TradeServiceStatus.PRE_APPROVED)) {
            // add to the list of approvers
            this.approvers = this.approvers + (this.approvers.isEmpty() ? "":",") + userId.toString();
            userId.toString();
        }

        // if the SI did not move forward, reset approval count back to 0 and approvers list reset
        if (status.equals(TradeServiceStatus.ABORTED) ||
                status.equals(TradeServiceStatus.DISAPPROVED) ||
                status.equals(TradeServiceStatus.RETURNED)) {
            this.approvers = "";
        }

        if(TradeServiceStatus.PENDING.equals(status)){
            details.remove("processDate");
        }

        if(TradeServiceStatus.PREPARED.equals(status)){
            details.put("processDate",DateUtil.convertToTfsDateString(new Date()));
        }
    }

    public IndemnityNumber getIndemnityNumber() {
        return indemnityNumber;
    }

    public void updateIndemnityNumber(IndemnityNumber indemnityNumber) {
        this.indemnityNumber = indemnityNumber;
    }

    public void setFacilityDetails(String facilityIdTo, String facilityTypeTo) {

        if (facilityIdTo != null && !facilityIdTo.isEmpty()) {
            this.facilityId = facilityIdTo;
        }
        if (facilityTypeTo != null && !facilityTypeTo.isEmpty()) {
            this.facilityType = facilityTypeTo;
        }
        this.modifiedDate = new Date();
    }

    public void setCifNumber(String cifNumber){
    	this.cifNumber = cifNumber;
    }

    public void setCifName(String cifName){
        this.cifName = cifName;
    }

    public void setMainCifNumber(String mainCifNumber){
    	this.mainCifNumber = mainCifNumber;
    }
    
    public void setFacilityType(String facilityType){
    	this.facilityType = facilityType;
    }
    
    public void setFacilityId(String facilityId){
    	this.facilityId = facilityId;
    }
    
    public void setCifDetails(String cifNumberTo, String cifNameTo, String accountOfficerTo, String ccbdBranchUnitCodeTo, String allocationUnitCodeTo) {

        if (cifNumberTo != null && !cifNumberTo.isEmpty()) {
            this.cifNumber = cifNumberTo;
        }
        if (cifNameTo != null && !cifNameTo.isEmpty()) {
            this.cifName = cifNameTo;
        }
        if (accountOfficerTo != null && !accountOfficerTo.isEmpty()) {
            this.accountOfficer = accountOfficerTo;
        }
        if (ccbdBranchUnitCodeTo != null && !ccbdBranchUnitCodeTo.isEmpty()) {
            this.ccbdBranchUnitCode = ccbdBranchUnitCodeTo;
        }
        if (allocationUnitCodeTo != null && !allocationUnitCodeTo.isEmpty()) {
        	this.allocationUnitCode = allocationUnitCodeTo;
        }
        this.modifiedDate = new Date();
    }

    
    
    public void setMainCifDetails(String mainCifNumberTo, String mainCifNameTo) {

        if (mainCifNumberTo != null && !mainCifNumberTo.isEmpty()) {
            this.mainCifNumber = mainCifNumberTo;
        }
        if (mainCifNameTo != null && !mainCifNameTo.isEmpty()) {
            this.mainCifName = mainCifNameTo;
        }
        this.modifiedDate = new Date();
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public TradeServiceStatus getStatus() {
        return status;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public DocumentClass getDocumentClass() {
        return documentClass;
    }

    public DocumentSubType1 getDocumentSubType1() {
        return documentSubType1;
    }

    public DocumentSubType2 getDocumentSubType2() {
        return documentSubType2;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public BigDecimal getTotalServiceChargesAmount(Boolean isProductPaymentCurrencyNonPhp) {


        BigDecimal totalServiceChargesAmount = BigDecimal.ZERO;

        for (ServiceCharge serviceCharge : this.serviceCharges) {
            if (serviceCharge.getAmount() != null) {
                if (!serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX")) {
                    totalServiceChargesAmount = totalServiceChargesAmount.add(serviceCharge.getOriginalAmount());
                } else {
                    if (isProductPaymentCurrencyNonPhp) {
                        totalServiceChargesAmount = totalServiceChargesAmount.add(serviceCharge.getOriginalAmount());
                    }
                }
            }
        }

        return totalServiceChargesAmount;
    }

    public BigDecimal getTotalServiceChargesAmount(Boolean isProductPaymentCurrencyNonPhp, Boolean withDTRLoan) {

        BigDecimal totalServiceChargesAmount = BigDecimal.ZERO;

        for (ServiceCharge serviceCharge : this.serviceCharges) {
            if (serviceCharge.getAmount() != null) {
                if (!serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX") || !serviceCharge.getChargeId().toString().equalsIgnoreCase("DOCSTAMPS")) {
                    totalServiceChargesAmount = totalServiceChargesAmount.add(serviceCharge.getOriginalAmount());
                } else if (serviceCharge.getChargeId().toString().equalsIgnoreCase("DOCSTAMPS")) {
                    if (withDTRLoan) {
                        totalServiceChargesAmount = totalServiceChargesAmount.add(serviceCharge.getOriginalAmount());
                    }
                } else if (serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX")) {
                    if (isProductPaymentCurrencyNonPhp) {
                        totalServiceChargesAmount = totalServiceChargesAmount.add(serviceCharge.getOriginalAmount());
                    }
                }
            }
        }

        return totalServiceChargesAmount;
    }

    public BigDecimal getTotalServiceChargesAmount() {

        BigDecimal totalServiceChargesAmount = BigDecimal.ZERO;

        Iterator<ServiceCharge> it = this.serviceCharges.iterator();
        while (it.hasNext()) {
            ServiceCharge serviceCharge = it.next();

            if (serviceCharge.getAmount() != null) {
                System.out.println("serviceCharge.getAmount():"+serviceCharge.getAmount());
                totalServiceChargesAmount = totalServiceChargesAmount.add(serviceCharge.getAmount());
                System.out.println("totalServiceChargesAmount :"+totalServiceChargesAmount );
            }
        }

        return totalServiceChargesAmount;
    }

    public BigDecimal getProductChargeAmount() {
        if (this.productCharge != null) {
            return this.productCharge.getAmount();
        } else {
            return null;
        }
    }

    public Currency getProductChargeCurrency() {
        if (this.productCharge != null) {
            return this.productCharge.getCurrency();
        } else {
            return null;
        }
    }

    public Currency getServiceChargesCurrency() {
        if (this.serviceCharges != null && !this.serviceCharges.isEmpty()) {
            Iterator<ServiceCharge> it = this.serviceCharges.iterator();
            ServiceCharge charge = it.next();
            return charge.getOriginalCurrency();
        } else {
            return null;
        }
    }

    public ServiceInstructionId getServiceInstructionId() {
        return serviceInstructionId;
    }

    public void setServiceInstructionId(ServiceInstructionId serviceInstructionId) {
        this.serviceInstructionId = serviceInstructionId;
    }

    public TradeServiceReferenceNumber getTradeServiceReferenceNumber() {
        return tradeServiceReferenceNumber;
    }

    public Boolean isForReinstatement() {
    	//System.out.println("Reinstatement Flag >>>>>>> "  + reinstateFlag.toString());
    	if (reinstateFlag == null) {
            return Boolean.FALSE;
        } else {
            return reinstateFlag;
        }
    }
    
    //ER: 20160517-099 - Add checking if expiry date = amendment date.
    public Boolean isForReinstatement(String expiryDateTest) throws ParseException {
    	//System.out.println("Reinstatement Flag >>>>>>> "  + reinstateFlag.toString());
    	SimpleDateFormat defaultFormat = new SimpleDateFormat("MM/dd/yyyy");
		//String reformattedDate = defaultFormat.format(defaultFormat.parse(expiryDate));
		Date dateEffect = defaultFormat.parse(expiryDateTest);
		
		String dateEffectStr = defaultFormat.format(defaultFormat.parse(expiryDateTest));
		String dateTodayStr = defaultFormat.format(new Date());
		
		System.out.println("New DATE FORMAT: " + dateEffect);
		System.out.println("date today: " + new Date());

		System.out.println("New DATE FORMAT STR: " + dateEffectStr);
		System.out.println("date today: STR:" + dateTodayStr);
		
		if (!documentSubType1.toString().equalsIgnoreCase("CASH") && (dateEffect.before(new Date()) && !dateEffectStr.equals(dateTodayStr))) {
			System.out.println("reinstateFlag Details>>>>> Y");
			reinstateFlag = Boolean.TRUE;
			return Boolean.TRUE;
		}
				
        if (reinstateFlag == null) {
            return Boolean.FALSE;
        } else {
            return reinstateFlag;
        }
        
    }

    public void paid() {
        this.paymentStatus = PaymentStatus.PAID;
    }

    public void unPay() {
        this.paymentStatus = PaymentStatus.UNPAID;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setAsNoPaymentRequired() {
        this.paymentStatus = PaymentStatus.NO_PAYMENT_REQUIRED;
    }

    // add an attachment to the service instruction
    public void addAttachment(String filename, String noderefid, Date createdDate, String attachmentType) {

        Attachment tAttachment = new Attachment(filename, noderefid, createdDate, attachmentType);
        this.attachments.add(tAttachment);
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void removeAttachment(String filename) {
        try {
            Attachment tholder = null;
            for (Attachment tAttach : this.attachments) {
                if (tAttach.getFilename() == filename) {
                    tholder = tAttach;
                }
            }
            this.attachments.remove(tholder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAttachment(String filename, String noderefid) {
        try {
            Attachment tholder = null;
            for (Attachment tAttach : this.attachments) {
                if (tAttach.getFilename() == filename && tAttach.getNoderefid() == noderefid) {
                    tholder = tAttach;
                }
            }
            this.attachments.remove(tholder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAttachment(String filename, String noderefid, String attachmentType) {

        try {
            Attachment tholder = null;
            for (Attachment tAttach : this.attachments) {
                if (tAttach.getFilename().equals(filename) && tAttach.getNoderefid().equals(noderefid) && attachmentType.equals(tAttach.getAttachmentType())) {
                    tholder = tAttach;
                }
            }
            this.attachments.remove(tholder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Currency getServiceChargeCurrency() {
        Currency currency = null;
        if(this.serviceCharges != null && !this.serviceCharges.isEmpty()) {
            Iterator<ServiceCharge> it = this.serviceCharges.iterator();
            ServiceCharge serviceCharge = it.next();
            if (serviceCharge.getAmount() != null) {
                currency = serviceCharge.getOriginalCurrency();
            }
        }

        return currency;
    }

    public Map<String, List<Object>> getChargesSummary() {

        Map<String, List<Object>> summaryMap = new HashMap<String, List<Object>>();

        List<Object> chargeHolderSC = new ArrayList();

        Iterator<ServiceCharge> it = this.serviceCharges.iterator();
        while (it.hasNext()) {
            ServiceCharge serviceCharge = it.next();
            if (serviceCharge.getAmount() != null) {
                chargeHolderSC.add(serviceCharge);
            }
        }

        summaryMap.put("SC", chargeHolderSC);


        List<Object> chargeHolderPC = new ArrayList();
        chargeHolderPC.add(this.productCharge);
        summaryMap.put("PC", chargeHolderPC);

        return summaryMap;
    }

    public UserId getCreatedBy() {
        return createdBy;
    }

    public Currency getChargesCurrency() {
        return chargesCurrency;
    }

    public void setTSDOwner(UserId userId) {
        this.createdBy = userId;
    }
    
    // required documents
    public void addRequiredDocuments(List<RequiredDocument> requiredDocumentList) {
        requiredDocument.addAll(requiredDocumentList);
    }

    public void updateRequiredDocuments(Set<RequiredDocument> requiredDocumentSet) {
        requiredDocument.clear();
        requiredDocument.addAll(requiredDocumentSet);
    }

    public Set<RequiredDocument> getRequiredDocument() {
        return requiredDocument;
    }

    // instructions to the paying/accepting/negotiating bank
    public void addInstructionToBank(List<InstructionToBank> instructionToBankList) {
        instructionToBank.addAll(instructionToBankList);
    }

    public void updateInstructionToBank(Set<InstructionToBank> instructionToBankSet) {
        instructionToBank.clear();
        instructionToBank.addAll(instructionToBankSet);
    }

    public Set<InstructionToBank> getInstructionToBank() {
        return instructionToBank;
    }

    // additional conditions
    public void addAdditionalCondition(List<AdditionalCondition> additionalConditionList) {
        additionalCondition.addAll(additionalConditionList);
    }

    public void updateAdditionalCondition(Set<AdditionalCondition> additionalConditionSet) {
        additionalCondition.clear();
        additionalCondition.addAll(additionalConditionSet);
    }

    public Set<AdditionalCondition> getAdditionalCondition() {
        return additionalCondition;
    }

     // transmittal letter
    public void addTransmittalLetter(List<TransmittalLetter> transmittalLetterList) {
        transmittalLetter.addAll(transmittalLetterList);
    }

    public void updateTransmittalLetter(Set<TransmittalLetter> transmittalLetterSet) {
        transmittalLetter.clear();
        transmittalLetter.addAll(transmittalLetterSet);
    }

    public Set<TransmittalLetter> getTransmittalLetter() {
        return transmittalLetter;
    }

    // swift charge
    public void addSwiftCharge(List<SwiftCharge> swiftChargeList) {
        swiftCharge.addAll(swiftChargeList);
    }

    public void updateSwiftCharge(Set<SwiftCharge> swiftChargeSet) {
        swiftCharge.clear();
        swiftCharge.addAll(swiftChargeSet);
    }

    public Set<SwiftCharge> getSwiftCharge() {
        return swiftCharge;
    }

    public void updateServiceChargeRates(Map<String, Object> parameterMap){
        System.out.println("in updateServiceChargeRates");
        //TODO : IF DMLC no USD TO PHP

        //TODO: UPDATE THIS

        //String settlementCurrency = (String) parameterMap.get("settlementCurrency");// Change this to get currency to php
        String currency = (String) details.get("currency");// Change this to get currency to php

        Currency tmpCurrency = Currency.getInstance("PHP");
        BigDecimal conversionRate = new BigDecimal(1); //Default to php no conversion


        if(currency!=null && !currency.equalsIgnoreCase("")){
            tmpCurrency = Currency.getInstance(currency);

            if(currency.equalsIgnoreCase("PHP")){

                String conversionRateKey = "USD-PHP_text_special_rate";
                String strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_text_special_rate strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                }
                this.passOnRateUsdToPhpServiceCharge = conversionRate;

                conversionRateKey = "USD-PHP_urr";
                strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_urr strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                } else {
                    conversionRate = BigDecimal.ZERO;
                    System.out.println("Missing URR");
                }

                this.passOnUrrServiceCharge = conversionRate;

            }

            if(currency.equalsIgnoreCase("USD")){

                String conversionRateKey = "USD-PHP_text_special_rate_charges";
                String strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_text_special_rate strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                }
                this.specialRateUsdToPhpServiceCharge = conversionRate;

                conversionRateKey = "USD-PHP_text_pass_on_rate";
                strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_text_pass_on_rate strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                }
                this.passOnRateUsdToPhpServiceCharge = conversionRate;
                if (this.specialRateUsdToPhpServiceCharge == null){ this.specialRateUsdToPhpServiceCharge = conversionRate;}

                conversionRateKey = "USD-PHP_urr";
                strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_urr strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                }
                this.passOnUrrServiceCharge = conversionRate;
                this.specialRateUrr = conversionRate;


            }

            if(!currency.equalsIgnoreCase("USD")||!currency.equalsIgnoreCase("PHP")){
                String conversionRateKey = "USD-PHP_special_rate_charges";
                String strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_special_rate_charges strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                }
                this.specialRateUsdToPhpServiceCharge = conversionRate;

                conversionRateKey = "USD-PHP_text_pass_on_rate";
                strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_text_pass_on_rate strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                }
                this.passOnRateUsdToPhpServiceCharge =conversionRate;
                if (this.specialRateUsdToPhpServiceCharge == null){ this.specialRateUsdToPhpServiceCharge = conversionRate;}


                conversionRateKey = "USD-PHP_urr";
                strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_urr strConversionRate:"+strConversionRate);
                if(strConversionRate != null){
                    conversionRate = new BigDecimal(strConversionRate);
                }
                this.passOnUrrServiceCharge = conversionRate;
                this.specialRateUrr = conversionRate;
                if (this.specialRateUsdToPhpServiceCharge == null){ this.specialRateUsdToPhpServiceCharge = conversionRate;}
                if (this.passOnRateUsdToPhpServiceCharge == null){ this.passOnRateUsdToPhpServiceCharge = conversionRate;}

                String ThirdToPHPconversionRateKeySPECIAL = currency.trim().toUpperCase() + "-PHP_text_special_rate";
                String ThirdToUSDconversionRateKeySPECIAL = currency.trim().toUpperCase() + "-USD_text_special_rate";

                String ThirdToPHPconversionRateKeyPASSON = currency.trim().toUpperCase() + "-PHP_text_special_rate";
                String ThirdToUSDconversionRateKeyPASSON = currency.trim().toUpperCase() + "-USD_text_special_rate";

                String ThirdToUSD_strConversionRateSPECIAL = (String) parameterMap.get(ThirdToUSDconversionRateKeySPECIAL);
                String ThirdToPHP_strConversionRateSPECIAL = (String) parameterMap.get(ThirdToPHPconversionRateKeySPECIAL);

                String ThirdToUSD_strConversionRatePASSON = (String) parameterMap.get(ThirdToUSDconversionRateKeyPASSON);
                String ThirdToPHP_strConversionRatePASSON = (String) parameterMap.get(ThirdToPHPconversionRateKeyPASSON);

                System.out.println("ThirdToUSD_strConversionRate :"+ThirdToUSD_strConversionRateSPECIAL);
                System.out.println("ThirdToPHP_strConversionRate :"+ThirdToPHP_strConversionRateSPECIAL);
                System.out.println("ThirdToUSD_strConversionRate :"+ThirdToUSD_strConversionRatePASSON);
                System.out.println("ThirdToPHP_strConversionRate :"+ThirdToPHP_strConversionRatePASSON);


                if(ThirdToPHP_strConversionRateSPECIAL != null //&& !ThirdToPHP_strConversionRateSPECIAL.equalsIgnoreCase("")
                        && ThirdToPHP_strConversionRatePASSON != null //&& !ThirdToPHP_strConversionRatePASSON.equalsIgnoreCase("")
                        && ThirdToUSD_strConversionRateSPECIAL != null //&& !ThirdToUSD_strConversionRateSPECIAL.equalsIgnoreCase("")
                        && ThirdToUSD_strConversionRatePASSON != null //&& !ThirdToUSD_strConversionRatePASSON.equalsIgnoreCase("")
                        ){

                    System.out.println("WITHIN Everything is here");
                    //strConversionRate = (String) parameterMap.get(ThirdToPHP_strConversionRateSPECIAL);
                    strConversionRate = ThirdToPHP_strConversionRateSPECIAL;
                    System.out.println("ThirdToPHP_strConversionRateSPECIAL strConversionRate WITHIN:"+strConversionRate);
                    if(strConversionRate != null){
                        conversionRate = new BigDecimal(strConversionRate);
                    }
                    System.out.println("conversionRate:"+conversionRate);
                    this.specialRateThirdToPhpServiceCharge =conversionRate;

                    //strConversionRate = (String) parameterMap.get(ThirdToPHP_strConversionRatePASSON);
                    strConversionRate = ThirdToPHP_strConversionRatePASSON;
                    System.out.println("ThirdToPHP_strConversionRatePASSON strConversionRate WITHIN:"+strConversionRate);
                    if(strConversionRate != null){
                        conversionRate = new BigDecimal(strConversionRate);
                    }
                    System.out.println("conversionRate:"+conversionRate);
                    this.passOnRateThirdToPhpServiceCharge=conversionRate;
                    if (this.specialRateThirdToPhpServiceCharge == null){ this.specialRateThirdToPhpServiceCharge = conversionRate;}

                    //strConversionRate = (String) parameterMap.get(ThirdToUSDconversionRateKeySPECIAL);
                    strConversionRate = ThirdToUSD_strConversionRateSPECIAL;
                    System.out.println("ThirdToUSD_strConversionRateSPECIAL strConversionRate WITHIN:"+strConversionRate);
                    if(strConversionRate != null){
                        conversionRate = new BigDecimal(strConversionRate);
                    }
                    System.out.println("conversionRate:"+conversionRate);
                    this.specialRateThirdToUsdServiceCharge=conversionRate;

                    strConversionRate = ThirdToUSD_strConversionRatePASSON;
                    System.out.println("ThirdToUSD_strConversionRatePASSON strConversionRateWITHIN:"+strConversionRate);
                    if(strConversionRate != null){
                        conversionRate = new BigDecimal(strConversionRate);
                    }
                    System.out.println("conversionRate:"+conversionRate);
                    this.passOnRateThirdToUSDServiceCharge=conversionRate;
                    if (this.specialRateThirdToUsdServiceCharge == null){ this.specialRateThirdToUsdServiceCharge = conversionRate;}


                } else if(ThirdToUSD_strConversionRateSPECIAL != null
                        && ThirdToUSD_strConversionRatePASSON != null ){

                    strConversionRate = ThirdToUSD_strConversionRateSPECIAL;
                    System.out.println("ThirdToUSD_strConversionRateSPECIAL strConversionRate:"+strConversionRate);
                    if(strConversionRate != null){
                        conversionRate = new BigDecimal(strConversionRate);
                    }
                    System.out.println("conversionRate:"+conversionRate);
                    this.specialRateThirdToUsdServiceCharge=conversionRate;

                    strConversionRate = ThirdToUSD_strConversionRatePASSON;
                    System.out.println("ThirdToUSD_strConversionRatePASSON strConversionRate:"+strConversionRate);
                    if(strConversionRate != null){
                        conversionRate = new BigDecimal(strConversionRate);
                    }
                    System.out.println("conversionRate:"+conversionRate);
                    this.passOnRateThirdToUSDServiceCharge=conversionRate;
                    if (this.specialRateThirdToUsdServiceCharge == null){ this.specialRateThirdToUsdServiceCharge = conversionRate;}
                }
            }

        }
    }

    public BigDecimal getPassOnRateThirdToUsdServiceCharge() {
        return passOnRateThirdToUSDServiceCharge;
    }

    public BigDecimal getPassOnRateUsdToPhpServiceCharge() {
        return passOnRateUsdToPhpServiceCharge;
    }

    public BigDecimal getPassOnRateThirdToPhpServiceCharge() {
        return passOnRateThirdToPhpServiceCharge;
    }

    public BigDecimal getPassOnUrrServiceCharge() {
        return passOnUrrServiceCharge;
    }

    public BigDecimal getSpecialRateThirdToUsdServiceCharge() {
        return specialRateThirdToUsdServiceCharge;
    }

    public BigDecimal getSpecialRateUsdToPhpServiceCharge() {
        return specialRateUsdToPhpServiceCharge;
    }

    public BigDecimal getSpecialRateThirdToPhpServiceCharge() {
        return specialRateThirdToPhpServiceCharge;
    }

    public BigDecimal getSpecialRateUrrServiceCharge() {
        return specialRateUrrServiceCharge;
    }

    //TODO Delete before commit
    public void printRates(){

        // pass on rate for serviceCharge
        System.out.println("pass on rate for serviceCharge");
        System.out.println(passOnRateThirdToUSDServiceCharge);
        System.out.println(passOnRateUsdToPhpServiceCharge);
        System.out.println(passOnRateThirdToPhpServiceCharge);
        System.out.println(passOnUrrServiceCharge);

        // special rates for serviceCharge
        System.out.println("special rates for serviceCharge");
        System.out.println(specialRateThirdToUsdServiceCharge);
        System.out.println(specialRateUsdToPhpServiceCharge);
        System.out.println(specialRateThirdToPhpServiceCharge);
        System.out.println(specialRateUrrServiceCharge);

    }

    public void updateLastUser(UserActiveDirectoryId lastUser) {
        this.lastUser = lastUser;
    }

    public UserActiveDirectoryId getLastUser() {
        return lastUser;
    }

    public void updatePreparedBy(UserActiveDirectoryId preparedBy) {
        this.preparedBy = preparedBy;
    }

    public UserActiveDirectoryId getPreparedBy() {
        return preparedBy;
    }

    public String getCifName() {
        return cifName;
    }

    public String getCifNumber(){
        return cifNumber;
    }
    
    public String getMainCifNumber(){
    	return mainCifNumber;
    }
    
    public void setDocumentNumber(DocumentNumber documentNumber) {
    	this.documentNumber = documentNumber;
    }

    public void setTradeProductNumber(TradeProductNumber tradeProductNumber) {
        this.tradeProductNumber = tradeProductNumber;
    }

    public String getCcbdBranchUnitCode() {
        return ccbdBranchUnitCode;
    }

    public String getAllocationUnitCode() {
        return allocationUnitCode;
    }

    public BigDecimal getPassOnRateThirdToUSD() {
        return passOnRateThirdToUSD;
    }

    public BigDecimal getPassOnRateUsdToPhp() {
        return passOnRateUsdToPhp;
    }

    public BigDecimal getPassOnRateThirdToPhp() {
        return passOnRateThirdToPhp;
    }

    public BigDecimal getPassOnUrr() {
        return passOnUrr;
    }

    public BigDecimal getSpecialRateThirdToUsd() {
        return specialRateThirdToUsd;
    }

    public BigDecimal getSpecialRateUsdToPhp() {
        return specialRateUsdToPhp;
    }

    public BigDecimal getSpecialRateThirdToPhp() {
        return specialRateThirdToPhp;
    }

    public BigDecimal getSpecialRateUrr() {
        return specialRateUrr;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public String getProcessingUnitCode() {
        return processingUnitCode;
    }

    public void resetApprovers() {
        this.approvers = "";
    }

    //TODO:Please update to actual check
    public Boolean isIcDiscrepancy(){
        if(this.icDiscrepancy!=null){
            return icDiscrepancy;
        } else {
            return Boolean.FALSE;
        }
    }

    public void removeUnusedCharges(Boolean isProductPaymentCurrencyNonPhp, Boolean withDTRLoan) {
        // commented out since this encounters concurrent modification

//        for (ServiceCharge serviceCharge : this.serviceCharges) {
//            if (serviceCharge.getAmount() != null) {
//                if (serviceCharge.getChargeId().toString().equalsIgnoreCase("DOCSTAMPS")) {
//                    if (!withDTRLoan) {
//                        System.out.println("Removing docstamps");
//                        this.serviceCharges.remove(serviceCharge);
//                    }
//                } else if (serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX")) {
//                    if (!isProductPaymentCurrencyNonPhp) {
//                        System.out.println("Removing CILEX");
//                        this.serviceCharges.remove(serviceCharge);
//                    }
//                }
//            }
//        }

        Iterator it = this.serviceCharges.iterator();

        while(it.hasNext()) {
            ServiceCharge serviceCharge = (ServiceCharge) it.next();

            if (serviceCharge.getAmount() != null) {
                if (serviceCharge.getChargeId().toString().equalsIgnoreCase("DOCSTAMPS")) {
                    if (!withDTRLoan) {
                        System.out.println("Removing docstamps");
                        it.remove();
                    }
                } else if (serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX")) {
                    if (!isProductPaymentCurrencyNonPhp) {
                        System.out.println("Removing CILEX");
                        it.remove();
                    }
                }
            }
        }
    }

    public void removeUnusedCharges(Boolean isProductPaymentCurrencyNonPhp) {
        // commented out since this encounters concurrent modification

//        for (ServiceCharge serviceCharge : this.serviceCharges) {
//            if (serviceCharge.getAmount() != null
//                    && serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX")
//                    && !isProductPaymentCurrencyNonPhp) {
//                System.out.println("Removing CILEX");
//                this.serviceCharges.remove(serviceCharge);
//
//            }
//        }

        Iterator it = this.serviceCharges.iterator();

        while(it.hasNext()) {
            ServiceCharge serviceCharge = (ServiceCharge) it.next();

            if (serviceCharge.getAmount() != null
                    && serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX")
                    && !isProductPaymentCurrencyNonPhp) {
                System.out.println("Removing CILEX");
                it.remove();
            }
        }
    }

    public void removeNoCharges() {
        Iterator it = this.serviceCharges.iterator();

        while(it.hasNext()) {
            ServiceCharge serviceCharge = (ServiceCharge) it.next();
            it.remove();
        }
    }

    public void removeCilex() {
        System.out.println("removing cilex fromt tradeservice object");
        Iterator it = this.serviceCharges.iterator();

        while(it.hasNext()) {
            ServiceCharge serviceCharge = (ServiceCharge) it.next();

            if (serviceCharge.getChargeId().toString().equalsIgnoreCase("CILEX")) {
                System.out.println("Removing CILEX");
                it.remove();
            }
        }
    }

    public void setStatus(TradeServiceStatus tradeServiceStatus) {
        this.status = tradeServiceStatus;
    }

    public Map<String, Object> getSavedRates() {
        Map<String, Object> ratesList = new HashMap<String, Object>();

        ratesList.put("PASSONRATETHIRDTOPHP", passOnRateThirdToPhpServiceCharge);
        ratesList.put("PASSONRATETHIRDTOUSD", passOnRateThirdToUSDServiceCharge);
        ratesList.put("PASSONRATEUSDTOPHP", passOnRateUsdToPhpServiceCharge);
        ratesList.put("SPECIALRATETHIRDTOPHP", specialRateThirdToPhpServiceCharge);
        ratesList.put("SPECIALRATETHIRDTOUSD", specialRateThirdToUsdServiceCharge);
        ratesList.put("SPECIALRATEUSDTOPHP", specialRateUsdToPhpServiceCharge);
        ratesList.put("URR", passOnUrrServiceCharge);


        return ratesList;
    }

    public TradeProductNumber getTradeProductNumber() {
        return tradeProductNumber;
    }

    public void setTradeServiceReferenceNumber(TradeServiceReferenceNumber tradeServiceReferenceNumber) {
        this.tradeServiceReferenceNumber = tradeServiceReferenceNumber;
    }

    public void setUserActiveDirectoryId(UserActiveDirectoryId userActiveDirectoryId) {
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public BigDecimal getServiceCharge(ChargeId chargeId) {
        for (ServiceCharge serviceCharge : this.serviceCharges) {
            //System.out.println("serviceCharge :"+serviceCharge.getChargeId() +" looking for:"+chargeId);
            if (serviceCharge.getChargeId().toString().equalsIgnoreCase(chargeId.toString())){

                //System.out.println("serviceCharge amount:"+serviceCharge.getOriginalAmount()+" of "+chargeId);
                return serviceCharge.getOriginalAmount();
            }
        }
        return null;
    }

    public BigDecimal getServiceChargeDefault(ChargeId chargeId) {
        for (ServiceCharge serviceCharge : this.serviceCharges) {
            System.out.println("serviceCharge :"+serviceCharge.getChargeId() +" looking for:"+chargeId);
            if (serviceCharge.getChargeId().toString().equalsIgnoreCase(chargeId.toString())){

            	// comment by robin for issue 3759: EBC/EBP Negotiation and Settlement
            	// Postage is fixed at 400PHP or it's equivalent currency   	
            	if(chargeId.toString().equalsIgnoreCase("POSTAGE") && documentType.toString().equalsIgnoreCase("FOREIGN")){
            		if(documentClass.toString().equalsIgnoreCase("BP") || documentClass.toString().equalsIgnoreCase("BC")){
            			System.out.println("Document Class: " + documentClass.toString() + ".. Document Type: " + documentType.toString());
            			System.out.println("asd Test condition met");
        				System.out.println("serviceCharge getDefaultAmount: 400 of "+chargeId);
        				return new BigDecimal(400);
            		}       		
            	} else {
            		System.out.println("asd test condition not met");
            	}
            	//comment end
            	//R TEST
            	System.out.println("R TEST BC ");
            	//
            	
                System.out.println("serviceCharge getDefaultAmount:"+ serviceCharge.getDefaultAmount()+" of "+chargeId);
                return serviceCharge.getDefaultAmount();
            }
        }
        return null;
    }

    public BigDecimal getServiceChargeNoCwt(ChargeId chargeId) {
        for (ServiceCharge serviceCharge : this.serviceCharges) {
            System.out.println("serviceCharge :"+serviceCharge.getChargeId() +" looking for:"+chargeId);
            if (serviceCharge.getChargeId().toString().equalsIgnoreCase(chargeId.toString())){

                System.out.println("serviceCharge getNocwtAmount:"+serviceCharge.getNocwtAmount()+" of "+chargeId);
                return serviceCharge.getNocwtAmount();
            }
        }
        return null;
    }

    public Currency getServiceChargeCurrency(ChargeId chargeId) {
        for (ServiceCharge serviceCharge : this.serviceCharges) {
            //System.out.println("serviceCharge :"+serviceCharge.getChargeId() +" looking for:"+chargeId);
            if (serviceCharge.getChargeId().toString().equalsIgnoreCase(chargeId.toString())){

                //System.out.println("serviceCharge amount:"+serviceCharge.getOriginalAmount()+" of "+chargeId);
                //return serviceCharge.getOriginalAmount();
                return serviceCharge.getOriginalCurrency();
            }
        }
        return null;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Set<ServiceCharge> getServiceCharge() {
        return serviceCharges;
    }

//    public void setServiceCharge(Set<ServiceCharge> serviceChargeList) {
//        this.serviceCharges = new HashSet<ServiceCharge>();
//
//        for (ServiceCharge serviceCharge : serviceChargeList) {
//            this.serviceCharges.add(serviceCharge.duplicateServiceCharge());
//        }
//    }

    public void setServiceCharge(Set<ServiceCharge> serviceChargeList) {
        if (this.serviceCharges == null) {
            this.serviceCharges = new HashSet<ServiceCharge>();
        }

        for (ServiceCharge serviceCharge : serviceChargeList) {
            this.serviceCharges.add(serviceCharge.duplicateServiceCharge());
        }
    }

    public void setAttachment(Set<Attachment> attachmentSet) {
        if (this.attachments == null) {
            this.attachments = new HashSet<Attachment>();
        }

        for (Attachment attachment : attachmentSet) {
            this.attachments.add(attachment.duplicateAttachment());
        }
    }

    public String getAccountOfficer() {
        return accountOfficer;
    }

    public Set<ServiceCharge> getServiceCharges() {
        return serviceCharges;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void updateServiceCharges(Set<ServiceCharge> serviceCharges) {
        this.serviceCharges.clear();
        this.serviceCharges.addAll(serviceCharges);
    }

    public void updateProductRefundDetails(Set<ProductRefundDetail> productRefundDetails) {
        this.productRefundDetails.clear();
        this.productRefundDetails.addAll(productRefundDetails);
    }

    public Set<ProductRefundDetail> getProductRefundDetails() {
        return productRefundDetails;
    }

    public void deleteProductRefundDetails() {
        this.productRefundDetails.clear();
    }

    public void updateProductCollectibleDetails(Set<ProductCollectibleDetail> productCollectibleDetails) {
        this.productCollectibleDetails.clear();
        this.productCollectibleDetails.addAll(productCollectibleDetails);
    }

    public Set<ProductCollectibleDetail> getProductCollectibleDetails() {
        return productCollectibleDetails;
    }

    public void deleteProductCollectibleDetails() {
        this.productCollectibleDetails.clear();
    }

    public void updateServiceChargesForRefund(Set<ServiceCharge> setServiceCharges) {
        this.serviceCharges.clear();
        this.serviceCharges.addAll(setServiceCharges);
    }

    public void updateServiceChargesForCollectible(Set<ServiceCharge> setServiceCharges) {
        this.serviceCharges.clear();
        this.serviceCharges.addAll(setServiceCharges);
    }

    public void addOtherChargeDetail(OtherChargesDetail otherChargesDetail) {
        this.otherChargesDetails.add(otherChargesDetail);
    }

    public void deleteOtherChargeDetail(String id) {
        Iterator it = this.otherChargesDetails.iterator();
        while (it.hasNext()) {
            OtherChargesDetail ocd = (OtherChargesDetail)it.next();
            if (ocd.getId().equals(id)) {
                it.remove();
            }
        }
    }

    public Set<OtherChargesDetail> getOtherChargesDetails() {
        return otherChargesDetails;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }
    
    public void setSpecialRates(Map<String, BigDecimal> rates) {
    	System.out.println("The rates are: "+rates);
    	if(rates.get("specialRateThirdToUsd") != null){
    		this.specialRateThirdToUsd = rates.get("specialRateThirdToUsd");
    	}
    	if(rates.get("specialRateUsdToPhp") != null){
    		this.specialRateUsdToPhp = rates.get("specialRateUsdToPhp");
    	}
        if(rates.get("urr") != null){
    		this.passOnUrr = rates.get("urr");
    		this.specialRateUrr = rates.get("urr");
    	}
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public void setProcessingUnitCode(String processingUnitCode) {
        this.processingUnitCode = processingUnitCode;
    }

    public String getMainCifName() {
        return mainCifName;
    }

    public void setMainCifName(String mainCifName) {
        this.mainCifName = mainCifName;
    }

    public TradeService createCopy(TradeService tradeService) {
        return (TradeService) SerializationUtils.clone(tradeService);
    }
}

