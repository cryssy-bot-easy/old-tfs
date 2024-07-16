package com.ucpb.tfs.batch.report.dw;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

public class TradeProduct implements Cloneable{
	
	/*	PROLOGUE:
	 * 	(revision - additional objects used by other class)
	  	SCR/ER Number: IBD-15-0828-01
		SCR/ER Description: Comparison of Balances in DW and SIBS-GL
		[Revised by:] Jesse James Joson
		[Date revised:] 09/17/2015
		Program [Revision] Details: add new objects, and create getters and setters
    	INPUT: 67 new objects
    	OUTPUT: Daily_Master_GL_Summary.xls & Daily_Master_GL_DailyBalance_Summary.xls
    	PROCESS: Called by another class to set or get the records in each object declared
     */
	
	/**
 	(revision)
	SCR/ER Number: ER# 20160505-030
	SCR/ER Description: 1.  The LC 909-03-929-16-00198-8 was amended last March 18, 2016 – only Tenor was amended from sight to usance.
	 						The AE are okay, debit the contingent for sight and credit to usance. But the DW Allocation reported the LC once 
	 						and the ADB are not reported separately  for sight and usance.
						2.  Adjustment on Standby LC tagging was not correctly reported in DW
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 05/05/2016
	Program [Revision] Details: Added variable standbyTagging together with its getter and setter.
	Date deployment: 
	Member Type: JAVA
	Project: CORE
	Project Name: TradeProduct.java	
	 */
	
    private String creationTradeServiceId;
    private String applicationAccountId;
    private String facilityId;
    private String facilityType;
    private String customerId;
    private String accountStatusId = "-";
    private String branchId;
    private String entityId = "29";
    private String productId;
    private String outstandingBookCode;
    private String outstandingCurrencyId;
    private Date openDate;
    private Date closedDate;
    private Date cancelledDate;
    private Date maturityDate;
    private Date lastReinstatementDate;
    private BigDecimal outstandingContingentAssets;
    private Date negotiationDate;
    private BigDecimal phpOutstandingContingentAssets = BigDecimal.ZERO;
    private Date lastAmendmentDate;
    private Date billOfLadingDate;
    private BigDecimal negoAmount;
    private BigDecimal phpNegoAmount;
    private BigDecimal phpOutstandingContingentLiabilities;
    private String contingentAssetsGlNumber;
    private String settlementBookCode;
    private String transactionCode;
    private String clientCbCode;
    private BigDecimal outstandingContingentLiabilities = BigDecimal.ZERO;
    private String countryCode;
    private String importStatusCode;
    private String contingentLiabilitiesGlNumber;
    private String correspondingBank;
    private String mainCifNumber;
    private String externalClientTinNumber;
    private String externalClientCifNumber;
    private String externalClientFirstName;
    private String externalClientMiddleName;
    private String externalClientLastName;
    private String modeOfPayment;
    private int cashFlag;
    private String negoNumber;
    private BigDecimal cashAmount;
    private BigDecimal originalAmount;
    private String previousProductID;
    private String previousAssetsGlNumber;
    private String previousLiabilitiesGlNumber;
    private BigDecimal totalNegotiatedAmount;
    private String exceptionCode;
    private String officerCode;
    private String standbyTagging;
	
	private String day;
	private String acctno;
    private String gmctyp;
    private String title;
    private String bookcd;
    private BigDecimal dayOrg01;
	private BigDecimal dayPhp01;
    private BigDecimal dayOrg02;
    private BigDecimal dayPhp02;
    private BigDecimal dayOrg03;
    private BigDecimal dayPhp03;
    private BigDecimal dayOrg04;
    private BigDecimal dayPhp04;
    private BigDecimal dayOrg05;
    private BigDecimal dayPhp05;
    private BigDecimal dayOrg06;
    private BigDecimal dayPhp06;
    private BigDecimal dayOrg07;
    private BigDecimal dayPhp07;
    private BigDecimal dayOrg08;
    private BigDecimal dayPhp08;
    private BigDecimal dayOrg09;
    private BigDecimal dayPhp09;
    private BigDecimal dayOrg10;
    private BigDecimal dayPhp10;
    private BigDecimal dayOrg11;
    private BigDecimal dayPhp11;
    private BigDecimal dayOrg12;
    private BigDecimal dayPhp12;
    private BigDecimal dayOrg13;
    private BigDecimal dayPhp13;
    private BigDecimal dayOrg14;
    private BigDecimal dayPhp14;
    private BigDecimal dayOrg15;
    private BigDecimal dayPhp15;
    private BigDecimal dayOrg16;
    private BigDecimal dayPhp16;
    private BigDecimal dayOrg17;
    private BigDecimal dayPhp17;
    private BigDecimal dayOrg18;
    private BigDecimal dayPhp18;
    private BigDecimal dayOrg19;
    private BigDecimal dayPhp19;
    private BigDecimal dayOrg20;
    private BigDecimal dayPhp20;
	private BigDecimal dayOrg21;
    private BigDecimal dayPhp21;
    private BigDecimal dayOrg22;
    private BigDecimal dayPhp22;
    private BigDecimal dayOrg23;
    private BigDecimal dayPhp23;
    private BigDecimal dayOrg24;
    private BigDecimal dayPhp24;
    private BigDecimal dayOrg25;
    private BigDecimal dayPhp25;
    private BigDecimal dayOrg26;
    private BigDecimal dayPhp26;
    private BigDecimal dayOrg27;
    private BigDecimal dayPhp27;
    private BigDecimal dayOrg28;
    private BigDecimal dayPhp28;
    private BigDecimal dayOrg29;
    private BigDecimal dayPhp29;
    private BigDecimal dayOrg30;
    private BigDecimal dayPhp30;
	private BigDecimal dayOrg31;
    private BigDecimal dayPhp31;
    
    public BigDecimal getDayOrg01() {
		return dayOrg01;
	}

	public void setDayOrg01(BigDecimal dayOrg01) {
		this.dayOrg01 = dayOrg01;
	}

	public BigDecimal getDayPhp01() {
		return dayPhp01;
	}

	public void setDayPhp01(BigDecimal dayPhp01) {
		this.dayPhp01 = dayPhp01;
	}

	public BigDecimal getDayOrg02() {
		return dayOrg02;
	}

	public void setDayOrg02(BigDecimal dayOrg02) {
		this.dayOrg02 = dayOrg02;
	}

	public BigDecimal getDayPhp02() {
		return dayPhp02;
	}

	public void setDayPhp02(BigDecimal dayPhp02) {
		this.dayPhp02 = dayPhp02;
	}

	public BigDecimal getDayOrg03() {
		return dayOrg03;
	}

	public void setDayOrg03(BigDecimal dayOrg03) {
		this.dayOrg03 = dayOrg03;
	}

	public BigDecimal getDayPhp03() {
		return dayPhp03;
	}

	public void setDayPhp03(BigDecimal dayPhp03) {
		this.dayPhp03 = dayPhp03;
	}

	public BigDecimal getDayOrg04() {
		return dayOrg04;
	}

	public void setDayOrg04(BigDecimal dayOrg04) {
		this.dayOrg04 = dayOrg04;
	}

	public BigDecimal getDayPhp04() {
		return dayPhp04;
	}

	public void setDayPhp04(BigDecimal dayPhp04) {
		this.dayPhp04 = dayPhp04;
	}

	public BigDecimal getDayOrg05() {
		return dayOrg05;
	}

	public void setDayOrg05(BigDecimal dayOrg05) {
		this.dayOrg05 = dayOrg05;
	}

	public BigDecimal getDayPhp05() {
		return dayPhp05;
	}

	public void setDayPhp05(BigDecimal dayPhp05) {
		this.dayPhp05 = dayPhp05;
	}

	public BigDecimal getDayOrg06() {
		return dayOrg06;
	}

	public void setDayOrg06(BigDecimal dayOrg06) {
		this.dayOrg06 = dayOrg06;
	}

	public BigDecimal getDayPhp06() {
		return dayPhp06;
	}

	public void setDayPhp06(BigDecimal dayPhp06) {
		this.dayPhp06 = dayPhp06;
	}

	public BigDecimal getDayOrg07() {
		return dayOrg07;
	}

	public void setDayOrg07(BigDecimal dayOrg07) {
		this.dayOrg07 = dayOrg07;
	}

	public BigDecimal getDayPhp07() {
		return dayPhp07;
	}

	public void setDayPhp07(BigDecimal dayPhp07) {
		this.dayPhp07 = dayPhp07;
	}

	public BigDecimal getDayOrg08() {
		return dayOrg08;
	}

	public void setDayOrg08(BigDecimal dayOrg08) {
		this.dayOrg08 = dayOrg08;
	}

	public BigDecimal getDayPhp08() {
		return dayPhp08;
	}

	public void setDayPhp08(BigDecimal dayPhp08) {
		this.dayPhp08 = dayPhp08;
	}

	public BigDecimal getDayOrg09() {
		return dayOrg09;
	}

	public void setDayOrg09(BigDecimal dayOrg09) {
		this.dayOrg09 = dayOrg09;
	}

	public BigDecimal getDayPhp09() {
		return dayPhp09;
	}

	public void setDayPhp09(BigDecimal dayPhp09) {
		this.dayPhp09 = dayPhp09;
	}

	public BigDecimal getDayOrg10() {
		return dayOrg10;
	}

	public void setDayOrg10(BigDecimal dayOrg10) {
		this.dayOrg10 = dayOrg10;
	}

	public BigDecimal getDayPhp10() {
		return dayPhp10;
	}

	public void setDayPhp10(BigDecimal dayPhp10) {
		this.dayPhp10 = dayPhp10;
	}

	public BigDecimal getDayOrg11() {
		return dayOrg11;
	}

	public void setDayOrg11(BigDecimal dayOrg11) {
		this.dayOrg11 = dayOrg11;
	}

	public BigDecimal getDayPhp11() {
		return dayPhp11;
	}

	public void setDayPhp11(BigDecimal dayPhp11) {
		this.dayPhp11 = dayPhp11;
	}

	public BigDecimal getDayOrg12() {
		return dayOrg12;
	}

	public void setDayOrg12(BigDecimal dayOrg12) {
		this.dayOrg12 = dayOrg12;
	}

	public BigDecimal getDayPhp12() {
		return dayPhp12;
	}

	public void setDayPhp12(BigDecimal dayPhp12) {
		this.dayPhp12 = dayPhp12;
	}

	public BigDecimal getDayOrg13() {
		return dayOrg13;
	}

	public void setDayOrg13(BigDecimal dayOrg13) {
		this.dayOrg13 = dayOrg13;
	}

	public BigDecimal getDayPhp13() {
		return dayPhp13;
	}

	public void setDayPhp13(BigDecimal dayPhp13) {
		this.dayPhp13 = dayPhp13;
	}

	public BigDecimal getDayOrg14() {
		return dayOrg14;
	}

	public void setDayOrg14(BigDecimal dayOrg14) {
		this.dayOrg14 = dayOrg14;
	}

	public BigDecimal getDayPhp14() {
		return dayPhp14;
	}

	public void setDayPhp14(BigDecimal dayPhp14) {
		this.dayPhp14 = dayPhp14;
	}

	public BigDecimal getDayOrg15() {
		return dayOrg15;
	}

	public void setDayOrg15(BigDecimal dayOrg15) {
		this.dayOrg15 = dayOrg15;
	}

	public BigDecimal getDayPhp15() {
		return dayPhp15;
	}

	public void setDayPhp15(BigDecimal dayPhp15) {
		this.dayPhp15 = dayPhp15;
	}

	public BigDecimal getDayOrg16() {
		return dayOrg16;
	}

	public void setDayOrg16(BigDecimal dayOrg16) {
		this.dayOrg16 = dayOrg16;
	}

	public BigDecimal getDayPhp16() {
		return dayPhp16;
	}

	public void setDayPhp16(BigDecimal dayPhp16) {
		this.dayPhp16 = dayPhp16;
	}

	public BigDecimal getDayOrg17() {
		return dayOrg17;
	}

	public void setDayOrg17(BigDecimal dayOrg17) {
		this.dayOrg17 = dayOrg17;
	}

	public BigDecimal getDayPhp17() {
		return dayPhp17;
	}

	public void setDayPhp17(BigDecimal dayPhp17) {
		this.dayPhp17 = dayPhp17;
	}

	public BigDecimal getDayOrg18() {
		return dayOrg18;
	}

	public void setDayOrg18(BigDecimal dayOrg18) {
		this.dayOrg18 = dayOrg18;
	}

	public BigDecimal getDayPhp18() {
		return dayPhp18;
	}

	public void setDayPhp18(BigDecimal dayPhp18) {
		this.dayPhp18 = dayPhp18;
	}

	public BigDecimal getDayOrg19() {
		return dayOrg19;
	}

	public void setDayOrg19(BigDecimal dayOrg19) {
		this.dayOrg19 = dayOrg19;
	}

	public BigDecimal getDayPhp19() {
		return dayPhp19;
	}

	public void setDayPhp19(BigDecimal dayPhp19) {
		this.dayPhp19 = dayPhp19;
	}

	public BigDecimal getDayOrg20() {
		return dayOrg20;
	}

	public void setDayOrg20(BigDecimal dayOrg20) {
		this.dayOrg20 = dayOrg20;
	}

	public BigDecimal getDayPhp20() {
		return dayPhp20;
	}

	public void setDayPhp20(BigDecimal dayPhp20) {
		this.dayPhp20 = dayPhp20;
	}

	public BigDecimal getDayOrg21() {
		return dayOrg21;
	}

	public void setDayOrg21(BigDecimal dayOrg21) {
		this.dayOrg21 = dayOrg21;
	}

	public BigDecimal getDayPhp21() {
		return dayPhp21;
	}

	public void setDayPhp21(BigDecimal dayPhp21) {
		this.dayPhp21 = dayPhp21;
	}

	public BigDecimal getDayOrg22() {
		return dayOrg22;
	}

	public void setDayOrg22(BigDecimal dayOrg22) {
		this.dayOrg22 = dayOrg22;
	}

	public BigDecimal getDayPhp22() {
		return dayPhp22;
	}

	public void setDayPhp22(BigDecimal dayPhp22) {
		this.dayPhp22 = dayPhp22;
	}

	public BigDecimal getDayOrg23() {
		return dayOrg23;
	}

	public void setDayOrg23(BigDecimal dayOrg23) {
		this.dayOrg23 = dayOrg23;
	}

	public BigDecimal getDayPhp23() {
		return dayPhp23;
	}

	public void setDayPhp23(BigDecimal dayPhp23) {
		this.dayPhp23 = dayPhp23;
	}

	public BigDecimal getDayOrg24() {
		return dayOrg24;
	}

	public void setDayOrg24(BigDecimal dayOrg24) {
		this.dayOrg24 = dayOrg24;
	}

	public BigDecimal getDayPhp24() {
		return dayPhp24;
	}

	public void setDayPhp24(BigDecimal dayPhp24) {
		this.dayPhp24 = dayPhp24;
	}

	public BigDecimal getDayOrg25() {
		return dayOrg25;
	}

	public void setDayOrg25(BigDecimal dayOrg25) {
		this.dayOrg25 = dayOrg25;
	}

	public BigDecimal getDayPhp25() {
		return dayPhp25;
	}

	public void setDayPhp25(BigDecimal dayPhp25) {
		this.dayPhp25 = dayPhp25;
	}

	public BigDecimal getDayOrg26() {
		return dayOrg26;
	}

	public void setDayOrg26(BigDecimal dayOrg26) {
		this.dayOrg26 = dayOrg26;
	}

	public BigDecimal getDayPhp26() {
		return dayPhp26;
	}

	public void setDayPhp26(BigDecimal dayPhp26) {
		this.dayPhp26 = dayPhp26;
	}

	public BigDecimal getDayOrg27() {
		return dayOrg27;
	}

	public void setDayOrg27(BigDecimal dayOrg27) {
		this.dayOrg27 = dayOrg27;
	}

	public BigDecimal getDayPhp27() {
		return dayPhp27;
	}

	public void setDayPhp27(BigDecimal dayPhp27) {
		this.dayPhp27 = dayPhp27;
	}

	public BigDecimal getDayOrg28() {
		return dayOrg28;
	}

	public void setDayOrg28(BigDecimal dayOrg28) {
		this.dayOrg28 = dayOrg28;
	}

	public BigDecimal getDayPhp28() {
		return dayPhp28;
	}

	public void setDayPhp28(BigDecimal dayPhp28) {
		this.dayPhp28 = dayPhp28;
	}

	public BigDecimal getDayOrg29() {
		return dayOrg29;
	}

	public void setDayOrg29(BigDecimal dayOrg29) {
		this.dayOrg29 = dayOrg29;
	}

	public BigDecimal getDayPhp29() {
		return dayPhp29;
	}

	public void setDayPhp29(BigDecimal dayPhp29) {
		this.dayPhp29 = dayPhp29;
	}

	public BigDecimal getDayOrg30() {
		return dayOrg30;
	}

	public void setDayOrg30(BigDecimal dayOrg30) {
		this.dayOrg30 = dayOrg30;
	}

	public BigDecimal getDayPhp30() {
		return dayPhp30;
	}

	public void setDayPhp30(BigDecimal dayPhp30) {
		this.dayPhp30 = dayPhp30;
	}

	public BigDecimal getDayOrg31() {
		return dayOrg31;
	}

	public void setDayOrg31(BigDecimal dayOrg31) {
		this.dayOrg31 = dayOrg31;
	}

	public BigDecimal getDayPhp31() {
		return dayPhp31;
	}

	public void setDayPhp31(BigDecimal dayPhp31) {
		this.dayPhp31 = dayPhp31;
	}

    public String getAcctno() {
		return acctno;
	}

	public void setAcctno(String acctno) {
		this.acctno = acctno;
	}

	public String getGmctyp() {
		return gmctyp;
	}

	public void setGmctyp(String gmctyp) {
		this.gmctyp = gmctyp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBookcd() {
		return bookcd;
	}

	public void setBookcd(String bookcd) {
		this.bookcd = bookcd;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}


	// service type (Opening, Negotiation, Cancellation, etc)
    private String serviceType;
    // document type (Foreign, Domestic)
    private String documentType;

    private DocumentClass documentClass;
    // Regular, Cash, Standby
    private DocumentSubType1 documentSubType1;
    // Sight, Usance
    private String documentSubType2;

    private BigDecimal aggregateBalance;

    private String glAccountType;

    private static final BigDecimal NEGATIVE = new BigDecimal("-1");

    public String getApplicationAccountId() {
        return applicationAccountId;
    }

    public void setApplicationAccountId(String applicationAccountId) {
        this.applicationAccountId = applicationAccountId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountStatusId() {
        return StringUtils.isEmpty(accountStatusId) ? "-" : accountStatusId;
    }

    public void setAccountStatusId(String accountStatusId) {
        this.accountStatusId = accountStatusId;
    }

    public String getBranchId() {
    	return StringUtils.isEmpty(branchId) ? "909" : branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }


    public String getOutstandingBookCode() {
        return outstandingBookCode;
    }

    public void setOutstandingBookCode(String outstandingBookCode) {
        this.outstandingBookCode = outstandingBookCode;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOutstandingCurrencyId() {
        return outstandingCurrencyId;
    }

    public void setOutstandingCurrencyId(String outstandingCurrencyId) {
        this.outstandingCurrencyId = outstandingCurrencyId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getNegotiationDate() {
        return negotiationDate;
    }

    public void setNegotiationDate(Date negotiationDate) {
        this.negotiationDate = negotiationDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Date getLastAmendmentDate() {
        return lastAmendmentDate;
    }

    public void setLastAmendmentDate(Date lastAmendmentDate) {
        this.lastAmendmentDate = lastAmendmentDate;
    }

    public Date getLastReinstatementDate() {
        return lastReinstatementDate;
    }

    public void setLastReinstatementDate(Date lastReinstatementDate) {
        this.lastReinstatementDate = lastReinstatementDate;
    }

    public BigDecimal getPhpOutstandingContingentAssets() {
        return phpOutstandingContingentAssets;
    }

    public void setPhpOutstandingContingentAssets(BigDecimal phpOutstandingContingentAssets) {
        this.phpOutstandingContingentAssets = phpOutstandingContingentAssets;
    }

    public BigDecimal getOutstandingContingentAssets() {
        return outstandingContingentAssets;
    }

    public void setOutstandingContingentAssets(BigDecimal outstandingContingentAssets) {
        this.outstandingContingentAssets = outstandingContingentAssets;
    }

    public BigDecimal getPhpOutstandingContingentLiabilities() {
        return phpOutstandingContingentLiabilities;
    }

    public void setPhpOutstandingContingentLiabilities(BigDecimal phpOutstandingContingentLiabilities) {
        this.phpOutstandingContingentLiabilities = phpOutstandingContingentLiabilities;
    }

    public BigDecimal getOutstandingContingentLiabilities() {
        return outstandingContingentLiabilities != null ? NEGATIVE.multiply(outstandingContingentLiabilities) : outstandingContingentLiabilities;
    }

    public void setOutstandingContingentLiabilities(BigDecimal outstandingContingentLiabilities) {
        this.outstandingContingentLiabilities = outstandingContingentLiabilities;
    }

    public String getContingentAssetsGlNumber() {
        return contingentAssetsGlNumber;
    }

    public void setContingentAssetsGlNumber(String contingentAssetsGlNumber) {
        this.contingentAssetsGlNumber = contingentAssetsGlNumber;
    }

    public String getContingentLiabilitiesGlNumber() {
        return contingentLiabilitiesGlNumber;
    }

    public void setContingentLiabilitiesGlNumber(String contingentLiabilitiesGlNumber) {
        this.contingentLiabilitiesGlNumber = contingentLiabilitiesGlNumber;
    }

    public Date getBillOfLadingDate() {
        return billOfLadingDate;
    }

    public void setBillOfLadingDate(Date billOfLadingDate) {
        this.billOfLadingDate = billOfLadingDate;
    }

    private Date uaMaturityDate;

    public Date getUaMaturityDate() {
        return uaMaturityDate;
    }

    public void setUaMaturityDate(Date uaMaturityDate) {
        this.uaMaturityDate = uaMaturityDate;
    }

    public String getCorrespondingBank() {
        return correspondingBank;
    }

    public void setCorrespondingBank(String correspondingBank) {
        this.correspondingBank = correspondingBank;
    }

    //TODO: REFACTOR ME! --todo todo todo
    public String getImportStatusCode() {
    	if(DocumentSubType1.DEFFERED.equals(documentSubType1) || DocumentSubType1.REGULAR.equals(documentSubType1)){
    		if(!isSelfFunded()){
    			if(DocumentSubType1.DEFFERED.equals(documentSubType1)){
    				return "8";
    			}
    			return "7";
    		}
    	}
    	
    	if(amended()){
    		return "2";
    	}
    	
        return "7";
    }

    public void setImportStatusCode(String importStatusCode) {
        this.importStatusCode = importStatusCode;
    }


    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    public String getClientCbCode() {
        return clientCbCode;
    }

    public void setClientCbCode(String clientCbCode) {
        this.clientCbCode = clientCbCode;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public BigDecimal getPhpNegoAmount() {
        return phpNegoAmount;
    }

    public void setPhpNegoAmount(BigDecimal phpNegoAmount) {
        this.phpNegoAmount = phpNegoAmount;
    }

    public BigDecimal getNegoAmount() {
        return negoAmount;
    }

    public void setNegoAmount(BigDecimal negoAmount) {
        this.negoAmount = negoAmount;
    }

    public String getMainCifNumber() {
        return mainCifNumber;
    }

    public void setMainCifNumber(String mainCifNumber) {
        this.mainCifNumber = mainCifNumber;
    }

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public DocumentSubType1 getDocumentSubType1() {
		return documentSubType1 != null ? documentSubType1 : DocumentSubType1.DEFAULT;
	}

	public void setDocumentSubType1(String documentSubType1) {
		this.documentSubType1 = DocumentSubType1.getDocumentSubType1(documentSubType1);
	}

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public DocumentClass getDocumentClass() {
        return documentClass != null ? documentClass : DocumentClass.OTHERS;
    }

    public void setDocumentClass(String documentClass) {
        this.documentClass = DocumentClass.getDocumentClassByName(documentClass);
    }

    public String getDocumentSubType2() {
        return documentSubType2;
    }

    public void setDocumentSubType2(String documentSubType2) {
        this.documentSubType2 = documentSubType2;
    }

    public boolean isSelfFunded(){
		return "PHP".equals(outstandingCurrencyId);
	}
	
	public boolean amended(){
		return lastAmendmentDate != null;
	}

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public BigDecimal getAggregateBalance() {
        return aggregateBalance;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public void setAggregateBalance(BigDecimal aggregateBalance) {
        this.aggregateBalance = aggregateBalance;
    }

    public void setSettlementBookCode(String settlementBookCode) {
        System.out.println("setSettlementBookCode:"+settlementBookCode);
        this.settlementBookCode = settlementBookCode;
    }

    public String getSettlementBookCode() {
        return settlementBookCode;
    }

    public String getCreationTradeServiceId() {
        return creationTradeServiceId;
    }

    public void setCreationTradeServiceId(String creationTradeServiceId) {
        this.creationTradeServiceId = creationTradeServiceId;
    }

    public String getGlAccountType() {
        return glAccountType;
    }

    public void setGlAccountType(String glAccountType) {
        this.glAccountType = glAccountType;
    }

    public String getExternalClientTinNumber() {
        return externalClientTinNumber;
    }

    public void setExternalClientTinNumber(String externalClientTinNumber) {
        this.externalClientTinNumber = externalClientTinNumber;
    }

    public String getExternalClientCifNumber() {
        return externalClientCifNumber;
    }

    public void setExternalClientCifNumber(String externalClientCifNumber) {
        this.externalClientCifNumber = externalClientCifNumber;
    }

    public String getExternalClientFirstName() {
        return externalClientFirstName;
    }

    public void setExternalClientFirstName(String externalClientFirstName) {
        this.externalClientFirstName = externalClientFirstName;
    }

    public String getExternalClientMiddleName() {
        return externalClientMiddleName;
    }

    public void setExternalClientMiddleName(String externalClientMiddleName) {
        this.externalClientMiddleName = externalClientMiddleName;
    }

    public String getExternalClientLastName() {
        return externalClientLastName;
    }

    public void setExternalClientLastName(String externalClientLastName) {
        this.externalClientLastName = externalClientLastName;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setDocumentClass(DocumentClass documentClass) {
        this.documentClass = documentClass;
    }

    public void setDocumentSubType1(DocumentSubType1 documentSubType1) {
        this.documentSubType1 = documentSubType1;
    }

    public int getCashFlag() {
        return cashFlag;
    }

    public void setCashFlag(int cashFlag) {
        this.cashFlag = cashFlag;
    }
          
    public String getNegoNumber() {
		return negoNumber;
	}

	public void setNegoNumber(String negoNumber) {
		this.negoNumber = negoNumber;
	}

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

    public String getPreviousProductID() {
		return previousProductID;
	}

	public void setPreviousProductID(String previousProductID) {
		this.previousProductID = previousProductID;
	}

	public String getPreviousAssetsGlNumber() {
		return previousAssetsGlNumber;
	}

	public void setPreviousAssetsGlNumber(String previousAssetsGlNumber) {
		this.previousAssetsGlNumber = previousAssetsGlNumber;
	}

	public String getPreviousLiabilitiesGlNumber() {
		return previousLiabilitiesGlNumber;
	}

	public void setPreviousLiabilitiesGlNumber(String previousLiabilitiesGlNumber) {
		this.previousLiabilitiesGlNumber = previousLiabilitiesGlNumber;
	}
	
	public BigDecimal getTotalNegotiatedAmount() {
		return totalNegotiatedAmount;
	}

	public void setTotalNegotiatedAmount(BigDecimal totalNegotiatedAmount) {
		this.totalNegotiatedAmount = totalNegotiatedAmount;
	}

	public TradeProduct getCloneTradeProduct()
    {
    	try{
    		return (TradeProduct)this.clone();
    	}catch(CloneNotSupportedException cnse){
    		cnse.printStackTrace();
    		return null;
    	}	
    }

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

	public String getStandbyTagging() {
		return standbyTagging;
	}

	public void setStandbyTagging(String standbyTagging) {
		this.standbyTagging = standbyTagging;
	}
       
	public void removeCloseDate() {
		this.closedDate = null;
	}	
	
	public void removeDates() {
		this.maturityDate = null;
		this.lastReinstatementDate = null;
		this.lastAmendmentDate = null;
		this.negotiationDate = null;		
	}
	
}