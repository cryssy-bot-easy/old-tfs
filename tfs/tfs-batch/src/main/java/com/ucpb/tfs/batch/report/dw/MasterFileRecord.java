package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.Record;
import com.ucpb.tfs.batch.report.dw.formatter.BigDecimalFormatter;
import com.ucpb.tfs.batch.report.dw.formatter.DateFormatter;
import com.ucpb.tfs.batch.util.TimeIgnoringDateComparator;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 */
@Record
public class MasterFileRecord {

	/*	PROLOGUE:
		(revision)
		SCR/ER Number: 20151104-015
		SCR/ER Description: Wrong Closed date and status  of Regular LC part for all fully adjusted to Cash in Master.
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 10/23/2015
		Program [Revision] Details: Added conditions which compared NegotiationDate, LastAmendmentDate, LastReinstatementDate and MaturityDate
									to set the correct Closed Date on the record in the Master File.
		Date deployment: 11/04/2015
		Member Type: JAVA
		Project: CORE
		Project Name: MasterFileRecord.java
	*/
	
    private static final String DATE_FORMAT = "yyyyMMdd";
    public static final String NO_VALUE = "-";
    private static final String DEFAULT_VALUE = "";
    private String settlementBlockCode;

    private String creditFacilityCode;

    private String counterpartyCode;

    private String industryCode;

    private Appraisal appraisal;

    //defaulted to blank for schedule 3 and 4
    private String counterpartyTinNumber = DEFAULT_VALUE;

    private String externalClientTin;

    private String externalClientNumber;

    private String externalClientName;

    private final TradeProduct tradeProduct;

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    private static final TimeIgnoringDateComparator DATE_COMPARATOR = new TimeIgnoringDateComparator();
    
    public MasterFileRecord(TradeProduct tradeProduct){
    	this.tradeProduct = tradeProduct;
    }

    @Field(offset = 1, length = 30, align = Align.LEFT)
    public String getApplicationAccountId() {
        String result = tradeProduct.getApplicationAccountId();
        if (result != null) {
            result = result.replaceAll("-", "");
        }
        return result;
    }

    public void setApplicationAccountId(String applicationAccountId) {
        this.tradeProduct.setApplicationAccountId(applicationAccountId);
    }

    @Field(offset = 31, length = 20, align = Align.LEFT)
    public String getFacilityId() {
        return dashIfEmpty(tradeProduct.getFacilityId());
    }

    public void setFacilityId(String facilityId) {
        this.tradeProduct.setFacilityId(facilityId);
    }

    @Field(offset = 51, length = 30, align = Align.LEFT)
    public String getCustomerId() {
        return dashIfEmpty(tradeProduct.getCustomerId());
    }

    public void setCustomerId(String customerId) {
        this.tradeProduct.setCustomerId(customerId);
    }

    @Field(offset = 81, length = 10, align = Align.LEFT)
    public String getAccountStatusId() {
    	
        String accountStatus = tradeProduct.getAccountStatusId();
        if("CLOSED".equalsIgnoreCase(accountStatus)){
            return "TF-B";
            //return "CLOSED";
        }
        if("CANCELLED".equalsIgnoreCase(accountStatus)){
            return "TF-A";
        	//return "CANCELLED";
        }
        
        if("EXPIRED".equalsIgnoreCase(accountStatus)){
        	return "TF-A";
         	//return "CANCELLED";
        }
        
        if("SETTLED".equalsIgnoreCase(accountStatus)){
        	return "TF-B";
        	//return "CLOSED";
        }           
        
        if("MATURED".equalsIgnoreCase(accountStatus)){
        	return "TF-D";
        	//return "MATURED";
        }
        
        if("-".equalsIgnoreCase(accountStatus)){
        	return "-";
        }

        return "TF-C";
        //return "CURRENT";
    }

    public void setAccountStatusId(String accountStatusId) {
        this.tradeProduct.setAccountStatusId(accountStatusId);
    }

    @Field(offset = 91, length = 3, align = Align.LEFT)
    public String getBranchId() {
        return tradeProduct.getBranchId();
    }

    public void setBranchId(String branchId) {
        this.tradeProduct.setBranchId(branchId);
    }

    @Field(offset = 94, length = 2, align = Align.LEFT)
    public String getOutstandingBookCode() {
        return tradeProduct.getOutstandingBookCode();
    }

    public void setOutstandingBookCode(String outstandingBookCode) {
        this.tradeProduct.setOutstandingBookCode(outstandingBookCode);
    }

    @Field(offset = 96, length = 10, align = Align.LEFT)
    public String getEntityId() {
        return tradeProduct.getEntityId();
    }

    public void setEntityId(String entityId) {
        this.tradeProduct.setEntityId(entityId);
    }

    @Field(offset = 106, length = 3, align = Align.LEFT)
    public String getOutstandingCurrencyId() {
        return tradeProduct.getOutstandingCurrencyId();
    }

    public void setOutstandingCurrencyId(String outstandingCurrencyId) {
        this.tradeProduct.setOutstandingCurrencyId(outstandingCurrencyId);
    }

    @Field(offset = 109, length = 15, align = Align.LEFT)
    public String getProductId() {
        return tradeProduct.getProductId();
    }

    public void setProductId(String productId) {
        this.tradeProduct.setProductId(productId);
    }

    @Field(offset = 124, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
    public Date getOpenDate() {
        return tradeProduct.getOpenDate();
    }

    public void setOpenDate(Date openDate) {
        this.tradeProduct.setOpenDate(openDate);
    }

    @Field(offset = 132, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getNegotiationDate() {
        return tradeProduct.getNegotiationDate();
    }

    public void setNegotiationDate(Date negotiationDate) {
        this.tradeProduct.setNegotiationDate(negotiationDate);
    }

    @Field(offset = 140, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getClosedDate() {
        //String accountStatus = tradeProduct.getAccountStatusId();
    	String accountStatus = tradeProduct.getAccountStatusId();
        if("CANCELLED".equalsIgnoreCase(accountStatus)){
            return tradeProduct.getCancelledDate();
        }
        
        if("EXPIRED".equalsIgnoreCase(accountStatus)){
        	Calendar tempExpiredDate = GregorianCalendar.getInstance();
        	tempExpiredDate.setTime(tradeProduct.getMaturityDate());
        	if(tradeProduct.getLastReinstatementDate() != null && tradeProduct.getMaturityDate() != null && tradeProduct.getNegotiationDate() != null){
	        	if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
	        			&& DATE_COMPARATOR.compare(tradeProduct.getNegotiationDate(), tradeProduct.getLastReinstatementDate()) < 0) {
	        		tempExpiredDate.setTime(tradeProduct.getLastReinstatementDate());
	        	} else if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getNegotiationDate()) < 0
	        			&& DATE_COMPARATOR.compare(tradeProduct.getLastReinstatementDate(), tradeProduct.getNegotiationDate()) < 0) {
	        		tempExpiredDate.setTime(tradeProduct.getNegotiationDate());
	        	} else if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getNegotiationDate()) < 0
	        			&& DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
	        			&& DATE_COMPARATOR.compare(tradeProduct.getLastReinstatementDate(), tradeProduct.getNegotiationDate()) == 0) {
	        		tempExpiredDate.setTime(tradeProduct.getLastReinstatementDate());
	        	}
        	}else if(tradeProduct.getLastReinstatementDate() != null && tradeProduct.getMaturityDate() != null){
	        	if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0) {
	        		tempExpiredDate.setTime(tradeProduct.getLastReinstatementDate());
	        	}
        	} else if(tradeProduct.getNegotiationDate() != null && tradeProduct.getMaturityDate() != null){
	        	if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getNegotiationDate()) < 0) {
	        		tempExpiredDate.setTime(tradeProduct.getNegotiationDate());
	        	}
        	}
        	tempExpiredDate.add(GregorianCalendar.DATE, +1);
			Date expiredDate = tempExpiredDate.getTime();
			return expiredDate;
        	//return tradeProduct.getMaturityDate();
        }

        if("CLOSED".equalsIgnoreCase(accountStatus)){
            return tradeProduct.getClosedDate();
        }

        return tradeProduct.getClosedDate();
    }

    public void setClosedDate(Date closedDate) {
    	if("CANCELLED".equalsIgnoreCase(tradeProduct.getAccountStatusId())){
    		this.tradeProduct.setCancelledDate(closedDate);
    	} else {
    		this.tradeProduct.setClosedDate(closedDate);
    	}
    }

    @Field(offset = 148, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getMaturityDate() {
        return tradeProduct.getMaturityDate();
    }

    public void setMaturityDate(Date maturityDate) {
        this.tradeProduct.setMaturityDate(maturityDate);
    }

    @Field(offset = 156, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getLastAmendmentDate() {
        return tradeProduct.getLastAmendmentDate();
    }

    public void setLastAmendmentDate(Date lastAmendmentDate) {
        this.tradeProduct.setLastAmendmentDate(lastAmendmentDate);
    }

    @Field(offset = 164, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getLastReinstatementDate() {
        return tradeProduct.getLastReinstatementDate();
    }

    public void setLastReinstatementDate(Date lastReinstatementDate) {
        this.tradeProduct.setLastReinstatementDate(lastReinstatementDate);
    }

    @Field(offset = 172, length = 24, align = Align.RIGHT, formatter = BigDecimalFormatter.class)
    public BigDecimal getPhpOutstandingContingentAssets() {
        return tradeProduct.getPhpOutstandingContingentAssets();
    }

    public void setPhpOutstandingContingentAssets(BigDecimal phpOutstandingContingentAssets) {
        this.tradeProduct.setPhpOutstandingContingentAssets(phpOutstandingContingentAssets);
    }

    @Field(offset = 196, length = 24, align = Align.RIGHT,formatter = BigDecimalFormatter.class)
    public BigDecimal getOutstandingContingentAssets() {

        if(tradeProduct.getOutstandingContingentAssets()!=null || !tradeProduct.getOutstandingContingentAssets().equals(BigDecimal.ZERO)){
            return new BigDecimal("10000").multiply(tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR)).setScale(0,BigDecimal.ROUND_FLOOR);
        } else {
            return  BigDecimal.ZERO;
        }

    }

    public void setOutstandingContingentAssets(BigDecimal outstandingContingentAssets) {
        this.tradeProduct.setOutstandingContingentAssets(outstandingContingentAssets);
    }

    @Field(offset = 220, length = 24, align = Align.RIGHT,formatter = BigDecimalFormatter.class)
    public BigDecimal getPhpOutstandingContingentLiabilities() {
        return tradeProduct.getPhpOutstandingContingentLiabilities();
    }

    public void setPhpOutstandingContingentLiabilities(BigDecimal phpOutstandingContingentLiabilities) {
        this.tradeProduct.setPhpOutstandingContingentLiabilities(phpOutstandingContingentLiabilities);
    }

    @Field(offset = 244, length = 24, align = Align.RIGHT,formatter = BigDecimalFormatter.class)
    public BigDecimal getOutstandingContingentLiabilities() {
        if(tradeProduct.getOutstandingContingentLiabilities()!=null || !tradeProduct.getOutstandingContingentLiabilities().equals(BigDecimal.ZERO)){
            return new BigDecimal("10000").multiply(tradeProduct.getOutstandingContingentLiabilities().setScale(2,BigDecimal.ROUND_FLOOR)).setScale(0,BigDecimal.ROUND_FLOOR);
        } else {
            return  BigDecimal.ZERO;
        }
    }

    public void setOutstandingContingentLiabilities(BigDecimal outstandingContingentLiabilities) {
        this.tradeProduct.setOutstandingContingentLiabilities(outstandingContingentLiabilities);
    }

    @Field(offset = 268, length = 19, align = Align.LEFT)
    public String getContingentAssetsGlNumber() {
        return tradeProduct.getContingentAssetsGlNumber();
    }

    public void setContingentAssetsGlNumber(String contingentAssetsGlNumber) {
        this.tradeProduct.setContingentAssetsGlNumber(contingentAssetsGlNumber);
    }

    @Field(offset = 287, length = 19, align = Align.LEFT)
    public String getContingentLiabilitiesGlNumber() {
        return tradeProduct.getContingentLiabilitiesGlNumber();
    }

    public void setContingentLiabilitiesGlNumber(String contingentLiabilitiesGlNumber) {
        this.tradeProduct.setContingentLiabilitiesGlNumber(contingentLiabilitiesGlNumber);
    }

    //@Field(offset = 306, length = 2, align = Align.LEFT)
    public String getSettlementBlockCode() {
        System.out.println("tradeProduct.getSettlementBookCode()"+tradeProduct.getSettlementBookCode());

        return tradeProduct.getSettlementBookCode();
    }

    public void setSettlementBlockCode(String settlementBlockCode) {
        tradeProduct.setSettlementBookCode(settlementBlockCode);
    }

    //@Field(offset = 308, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getBillOfLadingDate() {
        return tradeProduct.getBillOfLadingDate();
    }

    public void setBillOfLadingDate(Date billOfLadingDate) {
        this.tradeProduct.setBillOfLadingDate(billOfLadingDate);
    }

    //@Field(offset = 316, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getUaMaturityDate() {
        return tradeProduct.getUaMaturityDate();
    }

    public void setUaMaturityDate(Date uaMaturityDate) {
        this.tradeProduct.setUaMaturityDate(uaMaturityDate);
    }

    //@Field(offset = 324, length = 8, align = Align.RIGHT, formatter = DateFormatter.class)
//    @FixedFormatPattern(DATE_FORMAT)
    public Date getAppraisalDate() {
        return appraisal != null ? appraisal.getAppraisalDate() : null;
    }

    //@Field(offset = 332, length = 1, align = Align.LEFT)
    public String getCreditFacilityCode() {
        return creditFacilityCode;
    }

    public void setCreditFacilityCode(String creditFacilityCode) {
        this.creditFacilityCode = creditFacilityCode;
    }


    //@Field(offset = 333, length = 12, align = Align.LEFT)
    public String getCounterpartyCode() {
        return counterpartyCode;
    }

    public void setCounterpartyCode(String counterpartyCode) {
        this.counterpartyCode = counterpartyCode;
    }

    //@Field(offset = 345, length = 35, align = Align.LEFT)
    public String getCorrespondingBank() {
        return tradeProduct.getCorrespondingBank();
    }

    public void setCorrespondingBank(String correspondingBank) {
        this.tradeProduct.setCorrespondingBank(correspondingBank);
    }

    //@Field(offset = 380, length = 1, align = Align.LEFT)
    public String getImportStatusCode() {
        return tradeProduct.getImportStatusCode();
    }

    public void setImportStatusCode(String importStatusCode) {
        this.tradeProduct.setImportStatusCode(importStatusCode);
    }

    //@Field(offset = 381, length = 3, align = Align.LEFT)
    public String getCountryCode() {

        if(tradeProduct.getCountryCode()!=null && tradeProduct.getCountryCode().length()>3){
            String tempCountryCode = tradeProduct.getCountryCode();
            String[] tempStringArray = tempCountryCode.split(" - ");
//            System.out.println("tempCountryCode:"+tempCountryCode);
//            System.out.println("tempStringArray:"+tempStringArray);
            if(tempStringArray.length > 2 ){
                return  padLeft(tempStringArray[2],3,"0") ;
            } else {
                return padLeft(tradeProduct.getCountryCode(),3,"0") ;
            }

        } else {
            return padLeft(tradeProduct.getCountryCode(),3,"0") ;
        }
    }

    public void setCountryCode(String countryCode) {
        this.tradeProduct.setCountryCode(countryCode);
    }

    //@Field(offset = 384, length = 12, align = Align.LEFT)
    public String getClientCbCode() {
        return tradeProduct.getClientCbCode();
    }

    public void setClientCbCode(String clientCbCode) {
        this.tradeProduct.setClientCbCode(clientCbCode);
    }

    //@Field(offset = 396, length = 3, align = Align.LEFT)
    public String getTransactionCode() {
        //System.out.println("tradeProduct.getTransactionCode():"+tradeProduct.getTransactionCode());
        return tradeProduct.getTransactionCode();
    }

    public void setTransactionCode(String transactionCode) {
        this.tradeProduct.setTransactionCode(transactionCode);
    }

    //@Field(offset = 399, length = 1, align = Align.LEFT)
    public String getModeOfPayment() {
//        System.out.println("DocumentClass:"+tradeProduct.getDocumentClass());
//        System.out.println("ModeOfPayment:"+tradeProduct.getDocumentClass().getModeOfPayment());
        //Add logic here
        // others  - payment mode = 9
        // 'IB','UA',  mode of payment = 1
        // 'IB','UA',  mode of payment = 1
        // 'DR'     - payment mode  = 5
        // 'OA'      - payment mode = 4
        // 'DA'      - payment mode = 3
        // 'DP'      - payment mode = 2
        return tradeProduct.getDocumentClass().getModeOfPayment();
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.tradeProduct.setModeOfPayment(modeOfPayment);
    }

    //@Field(offset = 400, length = 5, align = Align.LEFT)
    public String getIndustryCode() {
        if(DocumentClass.LC.equals(tradeProduct.getDocumentClass())){
            return industryCode;
        }
        return DEFAULT_VALUE;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    //@Field(offset = 405, length = 2, align = Align.LEFT)
    public String getContingentType() {
        if(DocumentClass.LC.equals(tradeProduct.getDocumentClass())){
            return tradeProduct.getDocumentSubType1().getContingentType();
        }
        return DEFAULT_VALUE;
    }

    //@Field(offset = 407, length = 2, align = Align.LEFT)
    public String getSecurityCode() {
        if(DocumentClass.LC.equals(tradeProduct.getDocumentClass())){
            if(appraisal != null ){
                String temp = appraisal.getSecurityCode();
                if("010".equalsIgnoreCase(temp)){
                    return "21";
                } else if("022".equalsIgnoreCase(temp)){
                    return "22";
                } else if("024".equalsIgnoreCase(temp)){
                    return "22";
                } else {
                    return "29";
                }

            } else {
                return null;
            }
            //return appraisal != null ? appraisal.getSecurityCode() : null;
        }
        return DEFAULT_VALUE;
    }

    //@Field(offset = 409, length = 15, align = Align.RIGHT, formatter = BigDecimalFormatter.class)
    public BigDecimal getAppraisedValue() {
        if(DocumentClass.LC.equals(tradeProduct.getDocumentClass())){
            if(appraisal != null){
                if(appraisal.getAppraisedValue()!=null){
                    return  new BigDecimal("100").multiply(appraisal.getAppraisedValue());
                } else {
                    return  BigDecimal.ZERO;
                }
            }  else {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    //@Field(offset = 424, length = 25, align = Align.LEFT)
    public String getCounterpartyTinNumber() {
        return counterpartyTinNumber;
    }

    public void setCounterpartyTinNumber(String counterpartyTinNumber) {
        this.counterpartyTinNumber = counterpartyTinNumber;
    }

    //@Field(offset = 449, length = 25, align = Align.LEFT)
    public String getExternalClientTin() {
        return externalClientTin;
    }

    public void setExternalClientTin(String externalClientTin) {
        this.externalClientTin = externalClientTin;
    }

    //@Field(offset = 474, length = 30, align = Align.LEFT)
    public String getExternalClientNumber() {
        return externalClientNumber;
    }

    public void setExternalClientNumber(String externalClientNumber) {
        this.externalClientNumber = externalClientNumber;
    }

    //@Field(offset = 504, length = 35, align = Align.LEFT)
    public String getExternalClientName() {
        return externalClientName;
    }

    public void setExternalClientName(String externalClientName) {
        this.externalClientName = externalClientName;
    }

    //@Field(offset = 539, length = 24, align = Align.RIGHT)
    public BigDecimal getPhpNegoAmount() {
        return tradeProduct.getPhpNegoAmount();
    }

    public void setPhpNegoAmount(BigDecimal phpNegoAmount) {
        this.tradeProduct.setPhpNegoAmount(phpNegoAmount);
    }

    //@Field(offset = 563, length = 24, align = Align.RIGHT, formatter = BigDecimalFormatter.class)
    public BigDecimal getNegoAmount() {
        if(tradeProduct.getNegoAmount()!=null){
            return  new BigDecimal("100").multiply(tradeProduct.getNegoAmount());
        } else {
            return  BigDecimal.ZERO;
        }
    }

    public void setNegoAmount(BigDecimal negoAmount) {
        this.tradeProduct.setNegoAmount(negoAmount);
    }

	public Appraisal getAppraisal() {
		return appraisal;
	}

	public void setAppraisal(Appraisal appraisal) {
		this.appraisal = appraisal;
	}

    public DocumentClass getDocumentClass(){
        return tradeProduct.getDocumentClass();
    }

    public DocumentSubType1 getDocumentSubType1(){
        return tradeProduct.getDocumentSubType1();
    }

    public String getExceptionCode(){
    	return tradeProduct.getExceptionCode();
    }
    
    public String getOfficerCode(){
    	return tradeProduct.getOfficerCode();
    }
    
    public String export(){
        return String.format("%1$-30s%2$-20s%3$-30s%4$-10s%5$-3s%6$-2s" +
                "%7$-10s%8$-3s%9$-15s%10$-8s",
                getApplicationAccountId(),
                getFacilityId(),
                getCustomerId(),
                getAccountStatusId(),
                getBranchId(),
                tradeProduct.getOutstandingBookCode(),
                tradeProduct.getEntityId(),
                tradeProduct.getOutstandingCurrencyId(),
                tradeProduct.getProductId(),
                getDate(tradeProduct.getOpenDate()));


    }
    
    public String exportToExcel(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append("=\"" + getApplicationAccountId() + "\"" + COMMA);
    	str.append(getFacilityId() + COMMA);
    	str.append(getCustomerId() + COMMA);
    	str.append(getAccountStatusId() + COMMA);
    	str.append(getBranchId() + COMMA);
    	str.append(getOutstandingBookCode() + COMMA);
    	str.append(getEntityId() + COMMA);
    	str.append(getOutstandingCurrencyId() + COMMA);
    	str.append(getProductId() + COMMA);
    	str.append(getDate(getOpenDate()) + COMMA);
    	str.append(getDate(getNegotiationDate()) + COMMA);
    	str.append(getDate(getClosedDate()) + COMMA);
    	str.append(getDate(getMaturityDate()) + COMMA);
    	str.append(getDate(getLastAmendmentDate()) + COMMA);
    	str.append(getDate(getLastReinstatementDate()) + COMMA);
    	str.append("0" + COMMA);
    	str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getOutstandingContingentAssets())) + "\"" + COMMA);
    	str.append("0" + COMMA);
    	str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getOutstandingContingentLiabilities())) + "\"" + COMMA);
    	str.append("=\"" + getContingentAssetsGlNumber() + "\"" + COMMA);
    	str.append("=\"" + getContingentLiabilitiesGlNumber() + "\"" + COMMA);
    	
    	return str.toString();

    }
    
    public String exportToExcelException(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append(getExceptionCode() + COMMA);
    	str.append(getOfficerCode() + COMMA);
    	str.append("=\"" + getApplicationAccountId() + "\"" + COMMA);
    	str.append(getFacilityId() + COMMA);
    	str.append(getCustomerId() + COMMA);
    	str.append(getAccountStatusId() + COMMA);
    	str.append(getBranchId() + COMMA);
    	str.append(getOutstandingBookCode() + COMMA);
    	str.append(getEntityId() + COMMA);
    	str.append(getOutstandingCurrencyId() + COMMA);
    	str.append(getProductId() + COMMA);
    	str.append(getDate(getOpenDate()) + COMMA);
    	str.append(getDate(getNegotiationDate()) + COMMA);
    	str.append(getDate(getClosedDate()) + COMMA);
    	str.append(getDate(getMaturityDate()) + COMMA);
    	str.append(getDate(getLastAmendmentDate()) + COMMA);
    	str.append(getDate(getLastReinstatementDate()) + COMMA);
    	str.append("0" + COMMA);
    	str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getOutstandingContingentAssets())) + "\"" + COMMA);
    	str.append("0" + COMMA);
    	str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getOutstandingContingentLiabilities())) + "\"" + COMMA);
    	str.append("=\"" + getContingentAssetsGlNumber() + "\"" + COMMA);
    	str.append("=\"" + getContingentLiabilitiesGlNumber() + "\"" + COMMA);   	
    	
    	return str.toString();
    }
    
    public BigDecimal getAmountDividedBy10000(BigDecimal amount) {

        if(amount!=null || !amount.equals(BigDecimal.ZERO)){
            return amount.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_FLOOR);
        } else {
            return  BigDecimal.ZERO;
        }

    }

    private String getDate(Date date){
        if(date == null){
            return "0";
        }
        return DATE_FORMATTER.format(date);
    }

    private String dashIfEmpty(String value){
        return !StringUtils.isEmpty(value) ? value : NO_VALUE;
    }

    private String padLeft(String sourceString, int padLength, String padChar){
        StringBuilder result = new StringBuilder(sourceString != null ? sourceString : "");
        while(result.length() < padLength){
            result.insert(0,padChar);
        }
        return result.toString();
    }

}
