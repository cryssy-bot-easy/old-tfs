package com.ucpb.tfs.report.dw.regen;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.enums.ProductType;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: IPCVal
 */
public class TradeProductAudit implements Serializable {

    private Integer revId;
    private Short revType;

    private ProductType productType;
    private DocumentNumber documentNumber;
    private String cifNumber;
    private String cifName;
    private String mainCifNumber;
    private String mainCifName;
    private String accountOfficer;
    private String ccbdBranchUnitCode;
    private String facilityId;
    private String facilityType;
    private String facilityReferenceNumber;
    private String allocationUnitCode;
    private TradeProductStatus status;
    private String longName;
    private String address1;
    private String address2;
    private BigDecimal passOnRateThirdToUsd;
    private BigDecimal passOnRateThirdToPhp;
    private BigDecimal passOnRateUsdToPhp;
    private BigDecimal specialRateThirdToUsd;
    private BigDecimal specialRateThirdToPhp;
    private BigDecimal specialRateUsdToPhp;
    private BigDecimal urr;
    private BigDecimal amount;
    private Currency currency;
    private String firstName;
    private String middleName;
    private String lastName;
    private String tinNumber;
    private String exceptionCode;
    private String officerCode;
    private String processingUnitCode;

    public Integer getRevId() {
        return revId;
    }

    public void setRevId(Integer revId) {
        this.revId = revId;
    }

    public String getDocumentNumber() {
        return documentNumber.toString();
    }

    public void setDocumentNumber(DocumentNumber documentNumber) {
        this.documentNumber = documentNumber == null ? null : documentNumber;
    }

    public String getProductType() {
        return productType.toString();
    }

    public void setProductType(ProductType productType) {
        this.productType = productType == null ? null : productType;
    }

    public Short getRevType() {
        return revType;
    }

    public void setRevType(Short revType) {
        this.revType = revType;
    }

    public String getCifNumber() {
        return cifNumber;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber == null ? null : cifNumber.trim();
    }

    public String getCifName() {
        return cifName;
    }

    public void setCifName(String cifName) {
        this.cifName = cifName == null ? null : cifName.trim();
    }

    public String getMainCifNumber() {
        return mainCifNumber;
    }

    public void setMainCifNumber(String mainCifNumber) {
        this.mainCifNumber = mainCifNumber == null ? null : mainCifNumber.trim();
    }

    public String getMainCifName() {
        return mainCifName;
    }

    public void setMainCifName(String mainCifName) {
        this.mainCifName = mainCifName == null ? null : mainCifName.trim();
    }

    public String getAccountOfficer() {
        return accountOfficer;
    }

    public void setAccountOfficer(String accountOfficer) {
        this.accountOfficer = accountOfficer == null ? null : accountOfficer.trim();
    }

    public String getCcbdBranchUnitCode() {
        return ccbdBranchUnitCode;
    }

    public void setCcbdBranchUnitCode(String ccbdBranchUnitCode) {
        this.ccbdBranchUnitCode = ccbdBranchUnitCode == null ? null : ccbdBranchUnitCode.trim();
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId == null ? null : facilityId.trim();
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType == null ? null : facilityType.trim();
    }

    public String getFacilityReferenceNumber() {
        return facilityReferenceNumber;
    }

    public void setFacilityReferenceNumber(String facilityReferenceNumber) {
        this.facilityReferenceNumber = facilityReferenceNumber == null ? null : facilityReferenceNumber.trim();
    }

    public String getAllocationUnitCode() {
        return allocationUnitCode;
    }

    public void setAllocationUnitCode(String allocationUnitCode) {
        this.allocationUnitCode = allocationUnitCode == null ? null : allocationUnitCode.trim();
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(TradeProductStatus status) {
        this.status = status == null ? null : status;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName == null ? null : longName.trim();
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1 == null ? null : address1.trim();
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2 == null ? null : address2.trim();
    }

    public BigDecimal getPassOnRateThirdToUsd() {
        return passOnRateThirdToUsd;
    }

    public void setPassOnRateThirdToUsd(BigDecimal passOnRateThirdToUsd) {
        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
    }

    public BigDecimal getPassOnRateThirdToPhp() {
        return passOnRateThirdToPhp;
    }

    public void setPassOnRateThirdToPhp(BigDecimal passOnRateThirdToPhp) {
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
    }

    public BigDecimal getPassOnRateUsdToPhp() {
        return passOnRateUsdToPhp;
    }

    public void setPassOnRateUsdToPhp(BigDecimal passOnRateUsdToPhp) {
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
    }

    public BigDecimal getSpecialRateThirdToUsd() {
        return specialRateThirdToUsd;
    }

    public void setSpecialRateThirdToUsd(BigDecimal specialRateThirdToUsd) {
        this.specialRateThirdToUsd = specialRateThirdToUsd;
    }

    public BigDecimal getSpecialRateThirdToPhp() {
        return specialRateThirdToPhp;
    }

    public void setSpecialRateThirdToPhp(BigDecimal specialRateThirdToPhp) {
        this.specialRateThirdToPhp = specialRateThirdToPhp;
    }

    public BigDecimal getSpecialRateUsdToPhp() {
        return specialRateUsdToPhp;
    }

    public void setSpecialRateUsdToPhp(BigDecimal specialRateUsdToPhp) {
        this.specialRateUsdToPhp = specialRateUsdToPhp;
    }

    public BigDecimal getUrr() {
        return urr;
    }

    public void setUrr(BigDecimal urr) {
        this.urr = urr;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency.getCurrencyCode();
    }

    public void setCurrency(Currency currency) {
        this.currency = currency == null ? null : currency;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName == null ? null : firstName.trim();
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName == null ? null : middleName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName == null ? null : lastName.trim();
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber == null ? null : tinNumber.trim();
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode == null ? null : exceptionCode.trim();
    }

    public String getOfficerCode() {
        return officerCode;
    }

    public void setOfficerCode(String officerCode) {
        this.officerCode = officerCode == null ? null : officerCode.trim();
    }

    public String getProcessingUnitCode() {
        return processingUnitCode;
    }

    public void setProcessingUnitCode(String processingUnitCode) {
        this.processingUnitCode = processingUnitCode == null ? null : processingUnitCode.trim();
    }
}
