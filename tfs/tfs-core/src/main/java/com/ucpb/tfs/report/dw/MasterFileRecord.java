package com.ucpb.tfs.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.FixedFormatPattern;
import com.ancientprogramming.fixedformat4j.annotation.Record;

import java.math.BigDecimal;
import java.util.Date;

/**
 */
@Record
public class MasterFileRecord {

    private String applicationAccountId;

    private String facilityId;

    private String customerId;

    private String accountStatusId;

    private String branchId;

    private String outstandingBookCode;

    private String entityId;

    private String outstandingCurrencyId;

    private String productId;

    private Date openDate;

    private Date negotiationDate;

    private Date closedDate;

    private Date maturityDate;

    private Date lastAmendmentDate;

    private Date lastReinstatementDate;

    private BigDecimal phpOutstandingContingentAssets = BigDecimal.ZERO;

    private BigDecimal outstandingContingentAssets;

    private BigDecimal phpOutstandingContingentLiabilities = BigDecimal.ZERO;

    private BigDecimal outstandingContingentLiabilities;

    private String contingentAssetsGlNumber;

    private String contingentLiabilitiesGlNumber;

    private String settlementBlockCode;

    private Date billOfLadingDate;

    private Date uaMaturityDate;

    private Date appraisalDate;

    private String creditFacilityCode;

    private String counterpartyCode;

    private String correspondingBank;

    private String importStatusCode;

    private String countryCode;

    private String clientCbCode;

    private String transactionCode;

    private String modeOfPayment;

    private String industryCode;

    private String contingentType;

    private String securityCode;

    private BigDecimal appraisedValue;

    private String counterpartyTinNumber;

    private String externalClientTin;

    private String externalClientName;

    private BigDecimal phpNegoAmount;

    private BigDecimal negoAmount;

    private static final BigDecimal NEGATIVE = new BigDecimal("-1");

    @Field(offset = 1, length = 30)
    public String getApplicationAccountId() {
        return applicationAccountId;
    }

    public void setApplicationAccountId(String applicationAccountId) {
        this.applicationAccountId = applicationAccountId;
    }

    @Field(offset = 31, length = 20)
    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    @Field(offset = 51, length = 30)
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Field(offset = 81, length = 10)
    public String getAccountStatusId() {
        return accountStatusId;
    }

    public void setAccountStatusId(String accountStatusId) {
        this.accountStatusId = accountStatusId;
    }

    @Field(offset = 91, length = 3)
    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    @Field(offset = 94, length = 2)
    public String getOutstandingBookCode() {
        return outstandingBookCode;
    }

    public void setOutstandingBookCode(String outstandingBookCode) {
        this.outstandingBookCode = outstandingBookCode;
    }

    @Field(offset = 96, length = 10)
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Field(offset = 106, length = 3)
    public String getOutstandingCurrencyId() {
        return outstandingCurrencyId;
    }

    public void setOutstandingCurrencyId(String outstandingCurrencyId) {
        this.outstandingCurrencyId = outstandingCurrencyId;
    }

    @Field(offset = 109, length = 15)
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Field(offset = 124, length = 8)
    @FixedFormatPattern("yyyyMMdd")
    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    @Field(offset = 132, length = 8)
    @FixedFormatPattern("yyyyMMdd")
    public Date getNegotiationDate() {
        return negotiationDate;
    }

    public void setNegotiationDate(Date negotiationDate) {
        this.negotiationDate = negotiationDate;
    }

    @Field(offset = 140, length = 8)
    @FixedFormatPattern("yyyyMMdd")
    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    @Field(offset = 140, length = 8)
    @FixedFormatPattern("yyyyMMdd")
    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    @Field(offset = 148, length = 8)
    @FixedFormatPattern("yyyyMMdd")
    public Date getLastAmendmentDate() {
        return lastAmendmentDate;
    }

    public void setLastAmendmentDate(Date lastAmendmentDate) {
        this.lastAmendmentDate = lastAmendmentDate;
    }

    @Field(offset = 156, length = 8)
    @FixedFormatPattern("yyyyMMdd")
    public Date getLastReinstatementDate() {
        return lastReinstatementDate;
    }

    public void setLastReinstatementDate(Date lastReinstatementDate) {
        this.lastReinstatementDate = lastReinstatementDate;
    }

    @Field(offset = 164, length = 24, align = Align.RIGHT, paddingChar = '0')
    public BigDecimal getPhpOutstandingContingentAssets() {
        return phpOutstandingContingentAssets;
    }

    public void setPhpOutstandingContingentAssets(BigDecimal phpOutstandingContingentAssets) {
        this.phpOutstandingContingentAssets = phpOutstandingContingentAssets;
    }

    public BigDecimal getOutstandingContingentAssets() {
        return outstandingContingentAssets;
    }

    @Field(offset = 188, length = 24, align = Align.RIGHT, paddingChar = '0')
    public void setOutstandingContingentAssets(BigDecimal outstandingContingentAssets) {
        this.outstandingContingentAssets = outstandingContingentAssets;
    }

    @Field(offset = 212, length = 24, align = Align.RIGHT, paddingChar = '0')
    public BigDecimal getPhpOutstandingContingentLiabilities() {
        return phpOutstandingContingentLiabilities;
    }

    public void setPhpOutstandingContingentLiabilities(BigDecimal phpOutstandingContingentLiabilities) {
        this.phpOutstandingContingentLiabilities = phpOutstandingContingentLiabilities;
    }

    @Field(offset = 236, length = 24, align = Align.RIGHT, paddingChar = '0')
    public BigDecimal getOutstandingContingentLiabilities() {
        return NEGATIVE.multiply(outstandingContingentLiabilities);
    }

    public void setOutstandingContingentLiabilities(BigDecimal outstandingContingentLiabilities) {
        this.outstandingContingentLiabilities = outstandingContingentLiabilities;
    }

    @Field(offset = 260, length = 19, align = Align.RIGHT)
    public String getContingentAssetsGlNumber() {
        return contingentAssetsGlNumber;
    }

    public void setContingentAssetsGlNumber(String contingentAssetsGlNumber) {
        this.contingentAssetsGlNumber = contingentAssetsGlNumber;
    }

    @Field(offset = 279, length = 19, align = Align.RIGHT)
    public String getContingentLiabilitiesGlNumber() {
        return contingentLiabilitiesGlNumber;
    }

    public void setContingentLiabilitiesGlNumber(String contingentLiabilitiesGlNumber) {
        this.contingentLiabilitiesGlNumber = contingentLiabilitiesGlNumber;
    }

    //@Field(offset = 298, length = 2, align = Align.RIGHT, paddingChar = '0')
    public String getSettlementBlockCode() {
        return settlementBlockCode;
    }

    public void setSettlementBlockCode(String settlementBlockCode) {
        this.settlementBlockCode = settlementBlockCode;
    }

    //@Field(offset = 300, length = 8)
    //@FixedFormatPattern("yyyyMMdd")
    public Date getBillOfLadingDate() {
        return billOfLadingDate;
    }

    public void setBillOfLadingDate(Date billOfLadingDate) {
        this.billOfLadingDate = billOfLadingDate;
    }

    //@Field(offset = 308, length = 8)
    //@FixedFormatPattern("yyyyMMdd")
    public Date getUaMaturityDate() {
        return uaMaturityDate;
    }

    public void setUaMaturityDate(Date uaMaturityDate) {
        this.uaMaturityDate = uaMaturityDate;
    }

    //@Field(offset = 316, length = 8)
    //@FixedFormatPattern("yyyyMMdd")
    public Date getAppraisalDate() {
        return appraisalDate;
    }

    public void setAppraisalDate(Date appraisalDate) {
        this.appraisalDate = appraisalDate;
    }

    //@Field(offset = 324, length = 1)
    public String getCreditFacilityCode() {
        return creditFacilityCode;
    }

    public void setCreditFacilityCode(String creditFacilityCode) {
        this.creditFacilityCode = creditFacilityCode;
    }

    //@Field(offset = 325, length = 12)
    public String getCounterpartyCode() {
        return counterpartyCode;
    }

    public void setCounterpartyCode(String counterpartyCode) {
        this.counterpartyCode = counterpartyCode;
    }

    //@Field(offset = 336, length = 35)
    public String getCorrespondingBank() {
        return correspondingBank;
    }

    public void setCorrespondingBank(String correspondingBank) {
        this.correspondingBank = correspondingBank;
    }

    //@Field(offset = 371, length = 1)
    public String getImportStatusCode() {
        return importStatusCode;
    }

    public void setImportStatusCode(String importStatusCode) {
        this.importStatusCode = importStatusCode;
    }

    //@Field(offset = 372, length = 3)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    //@Field(offset = 375, length = 12)
    public String getClientCbCode() {
        return clientCbCode;
    }

    public void setClientCbCode(String clientCbCode) {
        this.clientCbCode = clientCbCode;
    }

    //@Field(offset = 387, length = 3)
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    //@Field(offset = 390, length = 1)
    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    //@Field(offset = 391, length = 5)
    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    //@Field(offset = 396, length = 2)
    public String getContingentType() {
        return contingentType;
    }

    public void setContingentType(String contingentType) {
        this.contingentType = contingentType;
    }

    //@Field(offset = 398, length = 2)
    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    //@Field(offset = 400, length = 15)
    public BigDecimal getAppraisedValue() {
        return appraisedValue;
    }

    public void setAppraisedValue(BigDecimal appraisedValue) {
        this.appraisedValue = appraisedValue;
    }

    //@Field(offset = 415, length = 25)
    public String getCounterpartyTinNumber() {
        return counterpartyTinNumber;
    }

    public void setCounterpartyTinNumber(String counterpartyTinNumber) {
        this.counterpartyTinNumber = counterpartyTinNumber;
    }

    //@Field(offset = 440, length = 25)
    public String getExternalClientTin() {
        return externalClientTin;
    }

    public void setExternalClientTin(String externalClientTin) {
        this.externalClientTin = externalClientTin;
    }

    //@Field(offset = 465, length = 35)
    public String getExternalClientName() {
        return externalClientName;
    }

    public void setExternalClientName(String externalClientName) {
        this.externalClientName = externalClientName;
    }

    //@Field(offset = 500, length = 24)
    public BigDecimal getPhpNegoAmount() {
        return phpNegoAmount;
    }

    //@Field(offset = 524, length = 24)
    public void setPhpNegoAmount(BigDecimal phpNegoAmount) {
        this.phpNegoAmount = phpNegoAmount;
    }

    //@Field(offset = 548, length = 24)
    public BigDecimal getNegoAmount() {
        return negoAmount;
    }

    public void setNegoAmount(BigDecimal negoAmount) {
        this.negoAmount = negoAmount;
    }
}
