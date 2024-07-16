package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.ProductType;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

@Audited
public class TradeProduct implements Serializable {

    private ProductType productType;

    protected String cifNumber;
    protected String cifName;
    protected String mainCifNumber;
    protected String mainCifName;
    protected String accountOfficer;
    protected String ccbdBranchUnitCode;
    protected String facilityId;
    protected String facilityType;
    protected String facilityReferenceNumber;
    protected String allocationUnitCode;

    protected DocumentNumber documentNumber;

    protected TradeProductStatus status;

    //CIF Address
    protected String longName;
    protected String address1;
    protected String address2;
    
    // Rates
    protected BigDecimal passOnRateThirdToUsd;
    protected BigDecimal passOnRateThirdToPhp;
    protected BigDecimal passOnRateUsdToPhp;
    protected BigDecimal specialRateThirdToUsd;
    protected BigDecimal specialRateThirdToPhp;
    protected BigDecimal specialRateUsdToPhp;
    protected BigDecimal urr;

    protected BigDecimal amount;
	protected Currency currency;
    
	protected String firstName;
	protected String middleName;
	protected String lastName;
	protected String tinNumber;
	
	protected String officerCode;
	protected String exceptionCode;
	
	private String processingUnitCode;
	
	// ITRS
	private String commodityCode;
	private String participantCode;

	public TradeProduct() {}

    public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public String getParticipantCode() {
		return participantCode;
	}

	public void setParticipantCode(String participantCode) {
		this.participantCode = participantCode;
	}

    // TODO: Pass Processing Unit Code to the Document Number generator
    // The Processing Unit Code is saved from the ETS
    public TradeProduct(DocumentNumber documentNumber, ProductType productType) {
        this.documentNumber = documentNumber;
        this.productType = productType;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(DocumentNumber documentNumber) {
        this.documentNumber = documentNumber;
    }

    public void updateStatus(TradeProductStatus tradeProductStatus) {
        this.status = tradeProductStatus;
    }

    public void updateAmount(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

//    public void updateCifDetails(String cifNumberTo, String cifNameTo, String accountOfficerTo, String ccbdBranchUnitCodeTo) {
    public void updateCifDetails(String cifNumberTo, String cifNameTo, String accountOfficerTo, String ccbdBranchUnitCodeTo, String longNameTo, String address1To, String address2To, String officerCodeTo, String exceptionCodeTo) {
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
        //added
        if (longNameTo != null && !longNameTo.isEmpty()) {
        	this.longName = longNameTo;
        }
        if (address1To != null && !address1To.isEmpty()) {
        	this.address1 = address1To;
        }
        if (address2To != null && !address2To.isEmpty()) {
        	this.address2 = address2To;
        }
        if (officerCodeTo != null && !officerCodeTo.isEmpty()) {
        	this.officerCode = officerCodeTo;
        }
        if (exceptionCodeTo != null && !exceptionCodeTo.isEmpty()) {
        	this.exceptionCode = exceptionCodeTo;
        }
    }

    public void updateMainCifDetails(String mainCifNumberTo, String mainCifNameTo) {
        if (mainCifNumberTo != null && !mainCifNumberTo.isEmpty()) {
            this.mainCifNumber = mainCifNumberTo;
        }
        if (mainCifNameTo != null && !mainCifNameTo.isEmpty()) {
            this.mainCifName = mainCifNameTo;
        }
    }

    public void updateFacilityDetails(String facilityId, String facilityType, String facilityReferenceNumber) {
        if (facilityId != null && !facilityId.isEmpty()) {
            this.facilityId = facilityId;
        }
        if (facilityType != null && !facilityType.isEmpty()) {
            this.facilityType = facilityType;
        }
        if (facilityReferenceNumber != null && !facilityReferenceNumber.isEmpty()) {
            this.facilityReferenceNumber = facilityReferenceNumber;
        }
    }

    public TradeProductStatus getStatus() {
        return status;
    }

    public Currency getCurrency() {
        return currency;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }

    public String getCifNumber() {
        return cifNumber;
    }

    public String getCifName() {
        return cifName;
    }

    public String getMainCifNumber() {
        return mainCifNumber;
    }

    public String getMainCifName() {
        return mainCifName;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityType() {
        return facilityType;
    }
    
    public void setAmount(BigDecimal amount) {
    	this.amount = amount;
    }

    public String getFacilityReferenceNumber() {
        return facilityReferenceNumber;
    }

    public void setFacilityReferenceNumber(String facilityReferenceNumber) {
        this.facilityReferenceNumber = facilityReferenceNumber;
    }

    public String getAllocationUnitCode() {
        return allocationUnitCode;
    }

    public void setAllocationUnitCode(String allocationUnitCode) {
        this.allocationUnitCode = allocationUnitCode;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public ProductType getProductType() {
        return productType;
    }

    public BigDecimal getPassOnRateThirdToUsd() {
        return passOnRateThirdToUsd;
    }

    public BigDecimal getPassOnRateThirdToPhp() {
        return passOnRateThirdToPhp;
    }

    public BigDecimal getPassOnRateUsdToPhp() {
        return passOnRateUsdToPhp;
    }

    public BigDecimal getSpecialRateThirdToUsd() {
        return specialRateThirdToUsd;
    }

    public BigDecimal getSpecialRateThirdToPhp() {
        return specialRateThirdToPhp;
    }

    public BigDecimal getSpecialRateUsdToPhp() {
        return specialRateUsdToPhp;
    }

    public BigDecimal getUrr() {
        return urr;
    }

    public String getProcessingUnitCode() {
        return processingUnitCode;
    }

    public void setProcessingUnitCode(String processingUnitCode) {
        this.processingUnitCode = processingUnitCode;
    }

	public String getTinNumber() {
		return tinNumber;
	}

	public void setTinNumber(String tinNumber) {
		this.tinNumber = tinNumber;
	}
}

