package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.FixedFormatPattern;
import com.ancientprogramming.fixedformat4j.annotation.Record;
import com.ucpb.tfs.batch.report.dw.formatter.BigDecimalFormatter;
import org.joda.time.DateMidnight;
import org.joda.time.Days;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 */
@Record
public class AllocationFileRecord implements Serializable, Cloneable {

	/* PROLOGUE:
	 * (revision)
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
		Project Name: AllocationFileRecord.java	
	 */


    private static final String CONTRA_ALLOC_UNIT = "10909";

    private Date creationDate = new Date();

    private Date bookingDate;

    private String applicationId = "TF";

    private String glAccountId;

    private String bookCode;

    private String allocationUnit;

    private String currencyId;

    private String applicationAccountId;

    private String customerId;

    private String productId;

    private BigDecimal phpTransactionAmount = BigDecimal.ZERO;

    private BigDecimal originalTransactionAmount;

    private BigDecimal usdTransactionAmount = BigDecimal.ZERO;

    private BigDecimal outstandingBalance;

    private String transactionType;

    private int lastRepricingDate = 0;

    private int nextReprisingDate = 0;

    private String contractTermDay = "0";

    private String contractTermType = "-";

    private String pastDueFlag = "-";

    private BigDecimal adbAmount;

    private BigDecimal pesoAdbAmount;

    private String branchUnitCode;
    
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    private Date openDate;

    private Date dateClosed;

    private Date cancellationDate;

    private String adbFlag;

    private String eventTransactionId;

    private String tradeServiceId;

    private String glAccountType;

    private Date issueDate;

    private int cashFlag;

    private DocumentType documentType;

    private DocumentSubType1 documentSubType1;
    
    private String negoNumber;
    
    private BigDecimal cashAmount;
    
    private BigDecimal originalAmount;
    
    private String previousProductID;
    
    private String previousAssetsGlNumber;
    
    private String previousLiabilitiesGlNumber;
    
    private BigDecimal totalNegotiatedAmount;
    
    private String documentNumber;
    
    private String glEntryType;
    
    private String exceptionCode;
    
    private String officerCode;
    
    private String standbyTagging;

	public AllocationFileRecord(){

    }

    public AllocationFileRecord(AllocationFileRecord source){
        this.creationDate = source.getCreationDate();
        this.bookingDate = source.getBookingDate();
        this.applicationId = source.getApplicationId();
        this.glAccountId = source.getGlAccountId();
        this.bookCode = source.getBookCode();
        this.allocationUnit = source.getAllocationUnit();
        this.currencyId = source.getCurrencyId();
        this.applicationAccountId = source.getApplicationAccountId();
        this.customerId = source.getCustomerId();
        this.productId = source.getProductId();
        this.originalTransactionAmount = source.getOriginalTransactionAmount();
        this.transactionType = source.getTransactionType();
        this.branchUnitCode = source.getBranchUnitCode();
        this.openDate = source.getOpenDate();
        this.dateClosed = source.getDateClosed();
        this.cancellationDate = source.getCancellationDate();
        this.adbFlag = source.getAdbFlag();
        this.eventTransactionId = source.getEventTransactionId();
        this.glAccountType = source.getGlAccountType();
        this.issueDate = source.getIssueDate();
        this.cashFlag = source.getCashFlag();
        this.documentType = source.getDocumentType();
        this.documentSubType1 = source.getDocumentSubType1();
        this.cashAmount =  source.getCashAmount();
        this.negoNumber = source.getNegoNumber();
        this.originalAmount = source.getOriginalAmount();
        this.previousAssetsGlNumber = source.getPreviousAssetsGlNumber();
        this.previousLiabilitiesGlNumber = source.getPreviousLiabilitiesGlNumber();
        this.previousProductID = source.getPreviousProductID();
        this.totalNegotiatedAmount = source.getTotalNegotiatedAmount();
        this.glEntryType = source.getGlEntryType();
    }


    @Field(offset = 1, length = 8, align = Align.RIGHT)
    @FixedFormatPattern("yyyyMMdd")
    public Date getCreationDate() {
        return creationDate;
    }
    
    @Field(offset = 9, length = 8, align = Align.RIGHT)
    @FixedFormatPattern("yyyyMMdd")
    public Date getBookingDate() {
        return bookingDate;
    }
    
    @Field(offset = 17, length = 2, align = Align.LEFT)
    public String getApplicationId() {
        return applicationId;
    }
    
    @Field(offset = 19, length = 30, align = Align.LEFT)
    public String getGlAccountId() {
        return glAccountId;
    }
    
    @Field(offset = 49, length = 2, align = Align.RIGHT)
    public String getBookCode() {
        return bookCode;
    }
    
    @Field(offset = 51, length = 5, align = Align.RIGHT)
    public String getAllocationUnit() {
        return allocationUnit;
    }
    
    @Field(offset = 56, length = 3, align = Align.RIGHT)
    public String getCurrencyId() {
        return currencyId;
    }
    
    @Field(offset = 59, length = 30, align = Align.LEFT)
    public String getApplicationAccountId() {
        String result = applicationAccountId;
        if (result != null) {
            result = result.replaceAll("-", "");
        }
        return result;
    }
    
    @Field(offset = 89, length = 30, align = Align.LEFT)
    public String getCustomerId() {
        return customerId;
    }
    
    @Field(offset = 119, length = 15, align = Align.LEFT)
    public String getProductId() {
        return productId;
    }
    
    @Field(offset = 134, length = 24, align = Align.RIGHT, formatter = BigDecimalFormatter.class)
    public BigDecimal getPhpTransactionAmount() {
        return phpTransactionAmount;
    }
    
    @Field(offset = 158, length = 24, align = Align.RIGHT, formatter = BigDecimalFormatter.class)
    public BigDecimal getOriginalTransactionAmount() {
        return originalTransactionAmount != null ? new BigDecimal("10000").multiply(originalTransactionAmount).setScale(0, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getOriginalTransactionAmountUnMultiplied() {
        return originalTransactionAmount != null ? originalTransactionAmount : BigDecimal.ZERO;
    }
    
    @Field(offset = 182, length = 24, align = Align.RIGHT, formatter = BigDecimalFormatter.class)
    public BigDecimal getUsdTransactionAmount() {
        return usdTransactionAmount;
    }
    
    @Field(offset = 206, length = 30, align = Align.LEFT)
    public String getTransactionType() {
        return transactionType;
    }
    
    @Field(offset = 236, length = 8, align = Align.RIGHT)
    public int getLastRepricingDate() {
        return lastRepricingDate;
    }

    @Field(offset = 244, length = 8, align = Align.RIGHT)
    public int getNextReprisingDate() {
        return nextReprisingDate;
    }
    
    @Field(offset = 252, length = 6, align = Align.RIGHT)
    public String getContractTermDay() {
        return contractTermDay;
    }

    @Field(offset = 258, length = 1, align = Align.LEFT)
    public String getContractTermType() {
        return contractTermType;
    }
    
    @Field(offset = 259, length = 1 , align = Align.LEFT)
    public String getPastDueFlag() {
        return pastDueFlag;
    }
    

    public BigDecimal getAdbAmount() {
        BigDecimal adb = BigDecimal.ZERO;
        if("Y".equalsIgnoreCase(adbFlag)){
            System.out.println("getApplicationAccountId():"+getApplicationAccountId());
            System.out.println("activeDays():"+activeDays());
            System.out.println("outstandingBalance:"+outstandingBalance);
            System.out.println("totalAmount:"+totalAmount);

            //ADD Handling for post issue date
            if(issueDate.compareTo(openDate) == -1 ){
                DateMidnight startingDate =  new DateMidnight(issueDate);
                DateMidnight endingDate =  new DateMidnight(openDate);
                int days = Days.daysBetween(startingDate,endingDate).getDays();
                System.out.println("days:"+days);

                Calendar cal0 = new GregorianCalendar();
                cal0.setTime(issueDate);
                cal0.set(Calendar.HOUR, 0);
                cal0.set(Calendar.MINUTE, 0);
                cal0.set(Calendar.MILLISECOND,0);
                Date tempIssueDate = cal0.getTime();
                System.out.println("end date:"+tempIssueDate);



                Calendar cal1 = new GregorianCalendar();
                cal1.setTime(bookingDate);
                cal1.set(Calendar.HOUR, 0);
                cal1.set(Calendar.MINUTE, 0);
                cal1.set(Calendar.MILLISECOND,0);
                Date tempDateEnd = cal1.getTime();
                System.out.println("end date:"+tempDateEnd);

                if(cal0.get(Calendar.YEAR)==cal1.get(Calendar.YEAR) &&
                        cal0.get(Calendar.MONTH)==cal1.get(Calendar.MONTH)
                        ){
                    if(totalAmount==null){
                        totalAmount =  BigDecimal.ZERO;
                    }
                    if(days>0 ){
                        System.out.println(originalTransactionAmount);
                        if(originalTransactionAmount!=null && totalAmount!=null){
                            System.out.println("totalAmount:"+totalAmount);
                            System.out.println("originalTransactionAmount:"+originalTransactionAmount);
                            totalAmount = totalAmount.add(new BigDecimal(days).multiply(originalTransactionAmount));
                        }

                    }
                }
            }


            if(totalAmount==null){
                return BigDecimal.ZERO;
            }
            BigDecimal adbTemp = totalAmount.divide(BigDecimal.valueOf(activeDays()),2,BigDecimal.ROUND_HALF_UP);
            adb = BigDecimal.ZERO.compareTo(adbTemp) == 0 ? BigDecimal.ZERO : adbTemp;
        }
        System.out.println("ADB AMOUNT:::"+adb.multiply(new BigDecimal("10000")));
        return adb.multiply(new BigDecimal("10000"));
    }


    @Field(offset = 260, length = 24, align = Align.RIGHT, formatter = BigDecimalFormatter.class)
    public BigDecimal getPesoAdbAmount() {
        System.out.println("pesoAdbAmount:"+pesoAdbAmount);
        if(pesoAdbAmount==null){
            return BigDecimal.ZERO;
        } else {
            return pesoAdbAmount.setScale(0, RoundingMode.HALF_UP);
        }
    }

    public void setPesoAdbAmount(BigDecimal pesoAdbAmount) {
        this.pesoAdbAmount = pesoAdbAmount;
    }




    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

   

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }


    public void setGlAccountId(String glAccountId) {
        this.glAccountId = glAccountId;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public void setAllocationUnit(String allocationUnit) {
        this.allocationUnit = allocationUnit;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public void setApplicationAccountId(String applicationAccountId) {
        this.applicationAccountId = applicationAccountId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setPhpTransactionAmount(BigDecimal phpTransactionAmount) {
        this.phpTransactionAmount = phpTransactionAmount;
    }

    public void setOriginalTransactionAmount(BigDecimal originalTransactionAmount) {
        this.originalTransactionAmount = originalTransactionAmount;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setLastRepricingDate(int lastRepricingDate) {
        this.lastRepricingDate = lastRepricingDate;
    }

    public void setNextReprisingDate(int nextReprisingDate) {
        this.nextReprisingDate = nextReprisingDate;
    }

    public void setContractTermDay(String contractTermDay) {
        this.contractTermDay = contractTermDay;
    }

    public void setContractTermType(String contractTermType) {
        this.contractTermType = contractTermType;
    }

    public void setPastDueFlag(String pastDueFlag) {
        this.pastDueFlag = pastDueFlag;
    }

    public void setAdbAmount(BigDecimal adbAmount) {
        this.adbAmount = adbAmount;
    }
    
    public void setUsdTransactionAmount(BigDecimal usdTransactionAmount) {
        this.usdTransactionAmount = usdTransactionAmount;
    }

	public String getBranchUnitCode() {
		return branchUnitCode;
	}

	public void setBranchUnitCode(String branchUnitCode) {
		this.branchUnitCode = branchUnitCode;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public String getAdbFlag() {
        return adbFlag;
    }

    public void setAdbFlag(String adbFlag) {
        this.adbFlag = adbFlag;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }
	
	public int activeDays(){
        System.out.println("old Start:"+getStartDate());
        System.out.println("old End:"+getEndDate());
        //int days = Days.daysBetween(getStartDate(),getEndDate()).getDays();

        Calendar cal = new GregorianCalendar();
        cal.setTime(bookingDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND,0);
        Date tempDateStart = cal.getTime();

        System.out.println("start date:"+tempDateStart);

        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(bookingDate);
        cal2.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.MILLISECOND,0);
        Date tempDateEnd = cal2.getTime();
        System.out.println("end date:"+tempDateEnd);

        DateMidnight startingDate =  new DateMidnight(tempDateStart);
        DateMidnight endingDate =  new DateMidnight(tempDateEnd);
        int days2 = Days.daysBetween(startingDate,endingDate).getDays();
        System.out.println("days2:"+days2);
        return days2 + 1 ;
//        if(days2>days){//Opened within the month
//            return days;
//        } else {
//            return days;
//        }
	}

    private DateMidnight getStartDate(){
        return new DateMidnight(openDate);
    }

    private DateMidnight getEndDate(){
        DateMidnight endingDate;
        if(dateClosed != null){
            endingDate =  new DateMidnight(dateClosed);
        }else if(cancellationDate != null){
            endingDate = new DateMidnight(cancellationDate);
        }else{
            endingDate = new DateMidnight();
        }
        return endingDate;
    }

    public String getEventTransactionId() {
        return eventTransactionId;
    }

    public void setEventTransactionId(String eventTransactionId) {
        this.eventTransactionId = eventTransactionId;
    }

    public String getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(String tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public static String getContraAllocUnit() {
        return CONTRA_ALLOC_UNIT;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public String getGlAccountType() {
        return glAccountType;
    }

    public void setGlAccountType(String glAccountType) {
        this.glAccountType = glAccountType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public int getCashFlag() {
        return cashFlag;
    }

    public void setCashFlag(int cashFlag) {
        this.cashFlag = cashFlag;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public DocumentSubType1 getDocumentSubType1() {
    	return documentSubType1 != null ? documentSubType1 : DocumentSubType1.DEFAULT;
    }

    public void setDocumentSubType1(DocumentSubType1 documentSubType1) {
        this.documentSubType1 = documentSubType1;
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

    public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	
	public String getGlEntryType() {
		return glEntryType;
	}

	public void setGlEntryType(String glEntryType) {
		this.glEntryType = glEntryType;
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

	public AllocationFileRecord getCloneAllocFileRecord()
    {
    	try{
    		return (AllocationFileRecord)this.clone();
    	}catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
    		return null;
		}
    }
	
	public String exportToExcel(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append(getDate(getCreationDate()) + COMMA);
    	str.append(getDate(getBookingDate()) + COMMA);
    	str.append(getApplicationId() + COMMA);
    	str.append("=\"" + getGlAccountId() + "\""  + COMMA);
    	str.append(getBookCode() + COMMA);
    	str.append(getAllocationUnit() + COMMA);
    	str.append(getCurrencyId() + COMMA);
    	str.append("=\"" + getApplicationAccountId() + "\"" + COMMA);
    	str.append(getCustomerId() + COMMA);
    	str.append(getProductId() + COMMA);
    	str.append("0" + COMMA);
    	str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getOriginalTransactionAmount())) + "\"" + COMMA);
    	str.append("0" + COMMA);
    	if(getTransactionType() != null){
    		str.append("\"" + getTransactionType() + "\"" + COMMA);
    	} else {
    		str.append("" + COMMA);
    	}
    	str.append("0" + COMMA);
    	str.append("0" + COMMA);
    	str.append("0" + COMMA);
    	str.append("-" + COMMA);
    	str.append("-" + COMMA);
    	if(getPesoAdbAmount() != null){
    		str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getPesoAdbAmount())) + "\"" + COMMA);
    	} else {
    		str.append("\"" + String.format("%,.2f",BigDecimal.ZERO) + "\"" + COMMA);
    	}
    	return str.toString();
    }
	
	public String exportToExcelException(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append(getExceptionCode() + COMMA);
    	str.append(getBranchUnitCode() + COMMA);
    	str.append(getOfficerCode() + COMMA);
    	str.append(getDate(getCreationDate()) + COMMA);
    	str.append(getDate(getBookingDate()) + COMMA);
    	str.append(getApplicationId() + COMMA);
    	str.append("=\"" + getGlAccountId() + "\""  + COMMA);
    	str.append(getBookCode() + COMMA);
    	str.append(getAllocationUnit() + COMMA);
    	str.append(getCurrencyId() + COMMA);
    	str.append("=\"" + getApplicationAccountId() + "\"" + COMMA);
    	str.append(getCustomerId() + COMMA);
    	str.append(getProductId() + COMMA);
    	str.append("0" + COMMA);
    	str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getOriginalTransactionAmount())) + "\"" + COMMA);
    	str.append("0" + COMMA);
    	if(getTransactionType() != null){
    		str.append("\"" + getTransactionType() + "\"" + COMMA);
    	} else {
    		str.append("" + COMMA);
    	}
    	str.append("0" + COMMA);
    	str.append("0" + COMMA);
    	str.append("0" + COMMA);
    	str.append("-" + COMMA);
    	str.append("-" + COMMA);
    	if(getPesoAdbAmount() != null){
    		str.append("\"" + String.format("%,.2f",getAmountDividedBy10000(getPesoAdbAmount())) + "\"" + COMMA);
    	} else {
    		str.append("\"" + String.format("%,.2f",BigDecimal.ZERO) + "\"" + COMMA);
    	}
    	
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
        SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
        return DATE_FORMATTER.format(date);
    }
}
