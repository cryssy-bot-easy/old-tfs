package com.ucpb.tfs.batch.report.dw.service;

import com.ucpb.tfs.batch.report.dw.AllocationFileRecord;
import com.ucpb.tfs.batch.report.dw.DocumentSubType1;
import com.ucpb.tfs.batch.report.dw.DocumentType;
import com.ucpb.tfs.batch.report.dw.dao.AllocationDao;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;
import com.ucpb.tfs.batch.util.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AllocationFileServiceImpl implements AllocationFileService{

	/*	PROLOGUE:
	 *	(revision)
		SCR/ER Number: ER# 20160505-030
		SCR/ER Description: 1.  The LC 909-03-929-16-00198-8 was amended last March 18, 2016 – only Tenor was amended from sight to usance.
		 						The AE are okay, debit the contingent for sight and credit to usance. But the DW Allocation reported the LC once 
		 						and the ADB are not reported separately  for sight and usance.
							2.  Adjustment on Standby LC tagging was not correctly reported in DW
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 05/05/2016
		Program [Revision] Details: 1.  Added condition to create a new document number with “88” for FOREIGN and “89” for DOMESTIC when the TENOR is
		 								changed from SIGHT to USANCE, indicating its corresponding ADB. 
									2.  Added condition to create a new document number with “78” for FOREIGN and “79” for DOMESTIC when the STANDBY TAGGING
									 	is changed  from PERFORMANCE to FINANCIAL or vice versa, , indicating its corresponding ADB.
		Date deployment: 
		Member Type: JAVA
		Project: CORE
		Project Name: AllocationFileServiceImpl.java	
	*/

    private static final String CREDIT_ALLOCATION_UNIT = "10909";
    private static final BigDecimal NEGATIVE = new BigDecimal("-1");
    private static final String STARTS_WITH_LETTER = "[a-zA-Z].*";
    private String mainOfficeBranch = "30001";
    public static final String CONTRA_UNIT = "10909";
    private static final String DATE_FORMAT = "MMddyy";

    private AllocationDao allocationDao;
	
	private TradeProductDao tradeProductDao;

    private SilverlakeLocalDao silverlakeLocalDao;

	private int getDaysInMonth(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}

    @Override
    public List<AllocationFileRecord> getProductAllocations(Date currentDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode) {

        List<AllocationFileRecord> incomesWithSplit = new ArrayList<AllocationFileRecord>(); //TO BE RETURNED For Letter Of Credit

        List<AllocationFileRecord> incomes = allocationDao.getAllocations(currentDate);

        System.out.println("income size:"+incomes.size());

        for (AllocationFileRecord income : incomes) {

            System.out.println(income.getApplicationAccountId()+"-"+income.getProductId()+"-"+income.getEventTransactionId());

//            String allocationUnitCodeOfficer = "";
//            if(income.getCustomerId()!=null){
//                try {
//                    System.out.println("Customer Id:" + income.getCustomerId());
//                    String officerCode = silverlakeLocalDao.getAccountOfficerOfCif(income.getCustomerId());
//                    income.setAllocationUnit(getAllocationUnitCode(officerCode));
//                    allocationUnitCodeOfficer = getAllocationUnitCode(officerCode);
//                } catch (Exception e){
//                    e.printStackTrace();
//                    System.out.println("");
//                    System.out.println("-----------------------------------------------------------------------------------");
//                    income.setAllocationUnit(treasuryAllocationCode);
//                }
//            } else {
//                //default
//                income.setAllocationUnit(treasuryAllocationCode);
//            }

            System.out.println("income.getAllocationUnit():"+income.getAllocationUnit());


            if(income.getAllocationUnit()==null || income.getAllocationUnit().isEmpty()){
            	income.setAllocationUnit("58" + income.getBranchUnitCode());
            	System.out.println("income.getAllocationUnit() null:"+income.getAllocationUnit());
                //income.setAllocationUnit(getMainOfficeBranch());
            }
            String documentNumber = income.getApplicationAccountId();

            //TODO Set correct accounting code here for the said document number
//            List<PaymentDetail> paymentDetails = allocationDao.getPaymentDetails(income.getTradeServiceId());
//            for (PaymentDetail paymentDetail : paymentDetails) {
//                try {
//
//                    BigDecimal passonThirdToUsd = paymentDetail.getPassOnRateThirdToUsd();
//                    BigDecimal passonUsdToPhp = paymentDetail.getPassOnRateUsdToPhp();
//                    BigDecimal urr = paymentDetail.getUrr();
//                    BigDecimal amount = paymentDetail.getAmount();
//                    BigDecimal specialUsdToPhp = paymentDetail.getSpecialRateUsdToPhp();
//                    String currency = "PHP";
//                    if(paymentDetail.getCurrency()!=null){
//                        currency = paymentDetail.getCurrency().toString();
//                    } else {
//                        currency="PHP";
//                    }
//
//
//                    System.out.println("urr:"+urr);
//                    System.out.println("passonThirdToUsd:"+passonThirdToUsd);
//                    System.out.println("passonUsdToPhp:"+passonUsdToPhp);
//                    System.out.println("specialUsdToPhp:"+specialUsdToPhp);
//                    System.out.println("amount:"+amount);
//                    System.out.println("currency:"+currency);
//
//                } catch ( Exception e){
//                    e.printStackTrace();
//                }
//            }


//            //TODO Split Allocation for rm and treasury
//            if(income.getGlAccountId().equalsIgnoreCase(fxProfitOrLossAccountingCode) && !income.getEventTransactionId().equalsIgnoreCase("BALANCING-ENTRY")){
//                AllocationFileRecord treasuryIncome = new AllocationFileRecord(income);
//                treasuryIncome.setAllocationUnit(treasuryAllocationCode);
//                //SPLIT
//                System.out.println("will split");
//                BigDecimal amountToBeSplit = income.getPhpTransactionAmount();
//
//                System.out.println("amountToBeSplit:"+amountToBeSplit);
//                //get all payments
//                //determine payment that caused the fx-profit-loss
//                // retrieve from plholder
//
//                BigDecimal oneCent = new BigDecimal("0.01");
//                List<ProfitLossHolder> plHolderEntries = allocationDao.getProfitLossHolderEntries(income.getTradeServiceId());
//                for (ProfitLossHolder plHolderEntry : plHolderEntries) {
//                    System.out.println("tradeServiceId:"+plHolderEntry.getTradeServiceId());
//                    System.out.println("PaymentDetailId:"+plHolderEntry.getPaymentDetailId());
//                    System.out.println("Profit Loss Amount:"+plHolderEntry.getProfitLossTotal());
//                    System.out.println("Payment Amount:"+plHolderEntry.getPaymentAmount());
//                    System.out.println("PL One Cent:"+plHolderEntry.getProfitLossOneCent());
//                    System.out.println("PL Other Cent:"+plHolderEntry.getProfitLossOtherCent());
//                    System.out.println("PL PassOnToSpecial:"+plHolderEntry.getProfitPassOnToSpecial());
//                    System.out.println("PL URRToPassOn:"+plHolderEntry.getProfitUrrToPassOn());
//
//                    if(plHolderEntry.getProfitLossTotal().compareTo(amountToBeSplit)==0 ||
//                            plHolderEntry.getProfitLossTotal().compareTo(amountToBeSplit.add(oneCent))==0 ||
//                            plHolderEntry.getProfitLossTotal().compareTo(amountToBeSplit.subtract(oneCent))==0 ){
//                        //Check if Pass On To Special is Zero which means use OnceCent and OtherOneCent
//                        if(plHolderEntry.getProfitPassOnToSpecial().compareTo(BigDecimal.ZERO)==0){
//                            treasuryIncome.setTotalAmount(plHolderEntry.getProfitLossOtherCent());
//                            treasuryIncome.setPhpTransactionAmount(plHolderEntry.getProfitLossOtherCent());
//                            income.setTotalAmount(plHolderEntry.getProfitLossOneCent());
//                            income.setPhpTransactionAmount(plHolderEntry.getProfitLossOneCent());
//                        } else {
//                            //Use plHolderEntry.getProfitPassOnToSpecial() and plHolderEntry.getProfitUrrToPassOn()
//                            treasuryIncome.setTotalAmount(plHolderEntry.getProfitUrrToPassOn());
//                            treasuryIncome.setPhpTransactionAmount(plHolderEntry.getProfitUrrToPassOn());
//                            income.setTotalAmount(plHolderEntry.getProfitPassOnToSpecial());
//                            income.setPhpTransactionAmount(plHolderEntry.getProfitPassOnToSpecial());
//                        }
//                    }
//                }
//                incomesWithSplit.add(income);
//                incomesWithSplit.add(treasuryIncome);
//            } else {
//                incomesWithSplit.add(income);
//            }

            incomesWithSplit.add(income);
        }

//        System.out.println("incomesWithSplit size:"+incomesWithSplit.size());
        return incomesWithSplit;
//        return  incomes;
    }

    @Override
    public List<AllocationFileRecord> getProductAverageDailyBalanceRecords(Date currentDate) {

        // LC
        List<AllocationFileRecord> activeProducts = allocationDao.getValidLetterOfCredits(currentDate);
        System.out.println("with LC size: " + activeProducts.size());

        // for adding a new record for LC Regular that is adjust to LC Cash OR change of tenor
        List<AllocationFileRecord> modifiedLcProduct = new ArrayList<AllocationFileRecord>();
        for(AllocationFileRecord allocationFileRecord:activeProducts)
        {
        	AllocationFileRecord icAllocationRecord = allocationFileRecord.getCloneAllocFileRecord();
        	
        	if(allocationFileRecord.getDocumentSubType1().equals(DocumentSubType1.REGULAR) && allocationFileRecord.getCashFlag() == 1){        	       		
        		//for creating new record with document number for Adjusted Regular to Cash
                AllocationFileRecord tempAllocFileRecord = allocationFileRecord.getCloneAllocFileRecord();
            	StringBuilder tempAccountId = new StringBuilder(tempAllocFileRecord.getApplicationAccountId());
            	StringBuilder tempDocuNumber = new StringBuilder(tempAllocFileRecord.getDocumentNumber());
                if(tempAccountId.toString().startsWith("FX")){
                    tempAccountId.replace(4,6,"98");
                    tempDocuNumber.replace(4, 6, "98");
                    tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                    tempAllocFileRecord.setProductId("TF113");
                    tempAllocFileRecord.setGlAccountId("246210101000");
                }else if(tempAccountId.toString().startsWith("DM")){
                    tempAccountId.replace(4,6,"99");
                    tempDocuNumber.replace(4, 6, "99");
                    tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                    tempAllocFileRecord.setProductId("TF217");
                    tempAllocFileRecord.setGlAccountId("246210102000");
                }else
                {
                    if(tempAllocFileRecord.getDocumentType().equals(DocumentType.FOREIGN)) {
                        tempAccountId.replace(3, 5, "98");
                        tempDocuNumber.replace(4, 6, "98");
                        tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                        tempAllocFileRecord.setProductId("TF113");
                        tempAllocFileRecord.setGlAccountId("246210101000");
                    }else{
                        tempAccountId.replace(3, 5, "99");
                        tempDocuNumber.replace(4, 6, "99");
                        tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                        tempAllocFileRecord.setProductId("TF217");
                        tempAllocFileRecord.setGlAccountId("246210102000");
                    }
                }
                tempAllocFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(tempDocuNumber.toString(), currentDate));
                tempAllocFileRecord.setOutstandingBalance(tempAllocFileRecord.getCashAmount());
                tempAllocFileRecord.setOpenDate(allocationDao.getAdjustmentCashOpenDate(allocationFileRecord.getDocumentNumber()));
                modifiedLcProduct.add(tempAllocFileRecord);// add modified LC to modifiedLcProduct
                
                allocationFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(allocationFileRecord.getDocumentNumber(), currentDate));
                allocationFileRecord.setDateClosed(allocationDao.getAdjustmentRegEndDate(allocationFileRecord.getDocumentNumber()));
            	allocationFileRecord.setGlAccountId(allocationFileRecord.getPreviousLiabilitiesGlNumber());
            	allocationFileRecord.setProductId(allocationFileRecord.getPreviousProductID());
            	
        	}else if(allocationFileRecord.getDocumentSubType1().equals(DocumentSubType1.REGULAR) && 
        			(allocationDao.checkIfTenorChange(allocationFileRecord.getDocumentNumber(), currentDate) != null && 
        			allocationDao.checkIfTenorChange(allocationFileRecord.getDocumentNumber(), currentDate).equalsIgnoreCase("Y"))){        	       		
        		//for creating new record with document number for Change of Tenor from Regular Sight to Usance
                AllocationFileRecord tempAllocFileRecord = allocationFileRecord.getCloneAllocFileRecord();
            	StringBuilder tempAccountId = new StringBuilder(tempAllocFileRecord.getApplicationAccountId());
            	StringBuilder tempDocuNumber = new StringBuilder(tempAllocFileRecord.getDocumentNumber());
                if(tempAccountId.toString().startsWith("FX")){
                    tempAccountId.replace(4,6,"88");
                    tempDocuNumber.replace(4, 6, "88");
                    tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                    tempAllocFileRecord.setProductId("TF112");
                    tempAllocFileRecord.setGlAccountId("835120102000");
                }else if(tempAccountId.toString().startsWith("DM")){
                    tempAccountId.replace(4,6,"89");
                    tempDocuNumber.replace(4, 6, "89");
                    tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                    tempAllocFileRecord.setProductId("TF212");
                    tempAllocFileRecord.setGlAccountId("835120101000");
                }else{
                    if(tempAllocFileRecord.getDocumentType().equals(DocumentType.FOREIGN)) {
                        tempAccountId.replace(3, 5, "88");
                        tempDocuNumber.replace(4, 6, "88");
                        tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                        tempAllocFileRecord.setProductId("TF112");
                        tempAllocFileRecord.setGlAccountId("835120102000");
                    }else{
                        tempAccountId.replace(3, 5, "89");
                        tempDocuNumber.replace(4, 6, "89");
                        tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                        tempAllocFileRecord.setProductId("TF212");
                        tempAllocFileRecord.setGlAccountId("835120101000");
                    }
                }
                tempAllocFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(tempDocuNumber.toString(), currentDate));
                modifiedLcProduct.add(tempAllocFileRecord);// add modified LC to modifiedLcProduct
                
                allocationFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(allocationFileRecord.getDocumentNumber(), currentDate));
            	allocationFileRecord.setGlAccountId(allocationFileRecord.getPreviousLiabilitiesGlNumber());
            	allocationFileRecord.setProductId(allocationFileRecord.getPreviousProductID());
            	
        	} else if(allocationFileRecord.getDocumentSubType1().equals(DocumentSubType1.STANDBY)){
        		List<Integer> revIdList = allocationDao.getAuditRevId(allocationFileRecord.getDocumentNumber(), currentDate);
        		Boolean isStandbyTaggingAdjusted = false;
        		String lastStandbyTagging = null;
        		System.out.println("----STANDBY---- DocNum: " + allocationFileRecord.getDocumentNumber());
        		for(Integer revId:revIdList){
        			List<AllocationFileRecord> letterOfCreditAuditList = allocationDao.getLetterOfCreditAuditByRevId(revId);
        			
        			Iterator<AllocationFileRecord> letterOfCreditAuditDetails = letterOfCreditAuditList.iterator();
        			while(letterOfCreditAuditDetails.hasNext() && isStandbyTaggingAdjusted == false){
        				AllocationFileRecord letterOfCreditAudit = letterOfCreditAuditDetails.next();
        				String standbyTagging = letterOfCreditAudit.getStandbyTagging();
        				
        				if(lastStandbyTagging != null && !lastStandbyTagging.equalsIgnoreCase(standbyTagging)){
        					isStandbyTaggingAdjusted = true;
        				} else {
        					lastStandbyTagging = standbyTagging;
        				}
        				System.out.println("isStandbyTaggingAdjusted: " + isStandbyTaggingAdjusted);
        				
        				if(isStandbyTaggingAdjusted){
        					//for creating new record with document number for Adjustment of Standby Tagging from Performance to Financial or Financial to Performance
        					AllocationFileRecord tempAllocFileRecord = allocationFileRecord.getCloneAllocFileRecord();
        	            	StringBuilder applicationAccountId = new StringBuilder(tempAllocFileRecord.getApplicationAccountId());
        	            	StringBuilder tempDocuNumber = new StringBuilder(tempAllocFileRecord.getDocumentNumber());
        	            	String openingStandbyTagging = allocationDao.getOpeningStandbyTagging(allocationFileRecord.getDocumentNumber());
        	            	Map<String, String> refProductInfo = new HashMap<String, String>();
        	            	System.out.println("openingStandbyTagging: " + openingStandbyTagging);
        	            	if(openingStandbyTagging.equalsIgnoreCase("PERFORMANCE")){       		
        	            		if(applicationAccountId.toString().startsWith("FX")){
        	            			refProductInfo = allocationDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "FINANCIAL");
            	                	applicationAccountId.replace(4,6,"78");
            	                	tempDocuNumber.replace(4,6,"78");
            	                	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
            	                	tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	                	tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else if(applicationAccountId.toString().startsWith("DM")){
            	                	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
            	                	applicationAccountId.replace(4,6,"79");
            	                	tempDocuNumber.replace(4,6,"79");
            	                	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
            	                	tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	                	tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else{
            	                	if(tempAllocFileRecord.getDocumentType().equals(DocumentType.FOREIGN)){
            	                		refProductInfo = allocationDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "FINANCIAL");
                	                	applicationAccountId.replace(3,5,"78");
                	                	tempDocuNumber.replace(4,6,"78");
                	                	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
                	                	tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
                	                	tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                	}else {
	        	                    	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
	        	                    	applicationAccountId.replace(3, 5, "79");
	        	                    	tempDocuNumber.replace(4, 6, "79");
	        	                    	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
	            	                    tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
	            	                    tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                	}
            	                }
        	            		
        	            		if(lastStandbyTagging.equalsIgnoreCase(openingStandbyTagging) == false){
            	            		if(applicationAccountId.toString().startsWith("FX") || allocationFileRecord.getDocumentType().equals(DocumentType.FOREIGN)){
            	            			refProductInfo = allocationDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "PERFORMANCE");
            	            			allocationFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	            			allocationFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else if(applicationAccountId.toString().startsWith("DM") || allocationFileRecord.getDocumentType().equals(DocumentType.DOMESTIC)){
                	                	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
            	            			allocationFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	            			allocationFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else{
            	                    	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
            	            			allocationFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	            			allocationFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
                	                }
        	            		}
        	            	} else if(openingStandbyTagging.equalsIgnoreCase("FINANCIAL")){
        	            		if(applicationAccountId.toString().startsWith("FX")){
        	                    	refProductInfo = allocationDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "PERFORMANCE");
            	                	applicationAccountId.replace(4,6,"78");
            	                	tempDocuNumber.replace(4,6,"78");
            	                	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
            	                	tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	                	tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else if(applicationAccountId.toString().startsWith("DM")){
        	                    	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
            	                	applicationAccountId.replace(4,6,"79");
            	                	tempDocuNumber.replace(4,6,"79");
            	                	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
            	                	tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	                	tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else{
            	                	if(tempAllocFileRecord.getDocumentType().equals(DocumentType.FOREIGN)){
            	                		refProductInfo = allocationDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "PERFORMANCE");
                	                	applicationAccountId.replace(3,5,"78");
                	                	tempDocuNumber.replace(4,6,"78");
                	                	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
                	                	tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
                	                	tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                	}else{
	        	                    	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
	        	                    	applicationAccountId.replace(3, 5, "79");
	        	                    	tempDocuNumber.replace(4, 6, "79");
	        	                    	tempAllocFileRecord.setApplicationAccountId(applicationAccountId.toString());
	            	                    tempAllocFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
	            	                    tempAllocFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
            	                	}
            	                }
        	            		
        	            		if(lastStandbyTagging.equalsIgnoreCase(openingStandbyTagging) == false){
             	            		if(applicationAccountId.toString().startsWith("FX") || allocationFileRecord.getDocumentType().equals(DocumentType.FOREIGN)){
            	            			refProductInfo = allocationDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "FINANCIAL");
            	            			allocationFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	            			allocationFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else if(applicationAccountId.toString().startsWith("DM") || allocationFileRecord.getDocumentType().equals(DocumentType.DOMESTIC)){
                	                	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
                	                	allocationFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	            			allocationFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else{
            	                    	refProductInfo = allocationDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
            	                    	allocationFileRecord.setGlAccountId(refProductInfo.get("CREDIT_CODE"));
            	            			allocationFileRecord.setProductId(refProductInfo.get("PRODUCTID"));
                	                }            	            		
        	            		}
        	            	}
        	            	System.out.println("New Application ID: " + tempAllocFileRecord.getApplicationAccountId());
        	            	tempAllocFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(tempDocuNumber.toString(), currentDate));
        	            	modifiedLcProduct.add(tempAllocFileRecord);// add modified trade product to modifiedLcProduct
        	            	
        	            	allocationFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(allocationFileRecord.getDocumentNumber(), currentDate));
        				}
        			}
        		}
        	} 
        	        	
//        	System.out.println("LC number: " + icAllocationRecord.getApplicationAccountId());
//        	List<Map<String, Object>> icAccounts = tradeProductDao.getIcAccountWithoutDash(icAllocationRecord.getApplicationAccountId(), currentDate);
//        	
//        	for(Map<String, Object> icAccount : icAccounts) {
//        		if (icAccount!=null) {
//        			String icNumber = icAccount.get("ICNUMBER").toString();
//                    String documentType = icAllocationRecord.getDocumentType().toString().toUpperCase();
////                	icNumber = icNumber.substring(0, icNumber.length()-1).concat("-").concat(icNumber.substring(icNumber.length()-1, icNumber.length()));
//                	BigDecimal icAdb = tradeProductDao.getAdbOfIcAccount(icNumber,currentDate);                	
//                	
//                	System.out.println("LC Account number: " + icAccount.get("DOCUMENTNUMBER"));
//                	System.out.println("IC Account number: " + icNumber);
//                	System.out.println("IC Document Type: " + documentType); 
//                	System.out.println("IC Amount: " + icAdb);            	
//                	icNumber = icNumber.replace("-", "");
//
//                	System.out.println("NEW IC Account number: " + icNumber);
//                	icAllocationRecord.setApplicationAccountId(icNumber);
//                	icAllocationRecord.setAdbAmount(icAdb);
//                	icAllocationRecord.setTotalAmount(icAdb);
//                	
//                	Map<String, String> icRefProductInfo = tradeProductDao.getRefProductInfo("LC", documentType, "NEGOTIATION", "DISCREPANCY", "NULL");
//                	icAllocationRecord.setProductId(icRefProductInfo.get("PRODUCTID"));
//                	icAllocationRecord.setGlAccountId(icRefProductInfo.get("CREDIT_CODE"));
//                	
//                    modifiedLcProduct.add(icAllocationRecord);
//            	}
//        	}  
        	
        	for(AllocationFileRecord test : modifiedLcProduct) {
            	System.out.println(" modifiedLcProduct DocNum: " + test.getApplicationAccountId());
        	}
        	
        	for(AllocationFileRecord test2 : activeProducts) {
            	System.out.println(" activeProducts DocNum: " + test2.getApplicationAccountId());
        	}
        }

        //add the trade product with new document number
        activeProducts.addAll(modifiedLcProduct);

        // DA
        activeProducts.addAll(allocationDao.getValidDocumentsAgainstAcceptance(currentDate));
        System.out.println("with DA size: " + activeProducts.size());

        // DP
        activeProducts.addAll(allocationDao.getValidDocumentsAgainstPayment(currentDate));
        System.out.println("with DP size: " + activeProducts.size());

        // OA not included based on TestScript
//        activeProducts.addAll(allocationDao.getValidOpenAccounts(currentDate));
//        System.out.println("with OA size: " + activeProducts.size());

        // DR not included based on TestScript
//        activeProducts.addAll(allocationDao.getValidDirectRemittances(currentDate));
//        System.out.println("with DR size: " + activeProducts.size());

        // BC
        activeProducts.addAll(allocationDao.getActiveBillsCollection(currentDate));
        System.out.println("with BC size: " + activeProducts.size());

        // Advance Payment not included based on TestScript
//        activeProducts.addAll(allocationDao.getActiveAdvancePayment(currentDate));
//        System.out.println("with Advance Payment size: " + activeProducts.size());

        // LC Advising not included based on TestScript
//        activeProducts.addAll(allocationDao.getActiveExportAdvising(currentDate));
//        System.out.println("with LC Advising size: " + activeProducts.size());

        //INSERT ADB for Bills Collection
//        activeProducts.addAll(allocationDao.get(currentDate));
//        System.out.println("with lc size:"+activeProducts.size());

        // Indemnity
        activeProducts.addAll(allocationDao.getActiveBankGuarantee(currentDate));
        System.out.println("with BG size: " + activeProducts.size());
        
        activeProducts.addAll(allocationDao.getIcAccounts(currentDate));
        System.out.println("with IC size: " + activeProducts.size());


//        List<AllocationFileRecord> adbs = getProductAverageDailyBalanceRecords(currentDate); //DAILY BALANCE

        System.out.println("activeProducts size: " + activeProducts.size());

        for (AllocationFileRecord adb : activeProducts) {

            //COMPUTE PESO AMOUNT
            System.out.println("Total AMOUNT = " + adb.getTotalAmount());
            System.out.println("Original ABD AMOUNT = " + adb.getAdbAmount());
            System.out.println("Original CURRENCY = " + adb.getCurrencyId());

            BigDecimal adbPesoAmount = BigDecimal.ZERO;
//          if (adb.getCurrencyId().trim().equalsIgnoreCase("PHP")) {

              adbPesoAmount = adb.getAdbAmount();  // Already rounds HALF UP

//          } else {
//
//              // Get first from current rates as this is the default batch behavior
//              BigDecimal revalRate = silverlakeLocalDao.getAngolConversionRate(adb.getCurrencyId().trim(), "PHP", 18);
//              if (revalRate == null ) {
//
//                  // If null, then get from historical rates
//
//                  // Below is for U2; hard-coded to Feb. 28, 2014 rates
//                  // Calendar calendar = Calendar.getInstance();
//                  // calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
//                  // calendar.set(Calendar.DATE, 28);
//                  // calendar.set(Calendar.YEAR, 2014);
//                  // Date historicalDate = calendar.getTime();
//
//                  Date historicalDate = currentDate;
//                  revalRate = silverlakeLocalDao.getHistoricalRevalRate(DateUtil.formatToInt(DATE_FORMAT, historicalDate), adb.getCurrencyId().trim(), "PHP", 18);
//                  System.out.println("Revaluation rate (" + DateUtil.formatToInt(DATE_FORMAT, historicalDate) + ") = " + revalRate.toPlainString());
//              } else {
//                  System.out.println("Revaluation rate, system date (" + DateUtil.formatToInt(DATE_FORMAT, currentDate) + ") = " + revalRate.toPlainString());
//              }
//              if (revalRate != null) {
//                  // Already rounds HALF UP
//                  adbPesoAmount = adb.getAdbAmount().multiply(revalRate);
//              } else {
//                  System.out.println(">>>>> NO REVAL RATE OBTAINED FOR CURRENCY = " + adb.getCurrencyId().trim() + ", date = " + DateUtil.formatToInt(DATE_FORMAT, currentDate));
//              }
//          }

             
            //Check ProductId if NON LC or Cash LC
            //If true reverse signs
            if ("A".equalsIgnoreCase(adb.getGlAccountType())) {
                adbPesoAmount = adbPesoAmount.negate();
            } 
            //else if("TF113".equalsIgnoreCase(adb.getProductId())  ||  "TF217".equalsIgnoreCase(adb.getProductId())){
            //    adbPesoAmount = adbPesoAmount.negate();
            //}

            adb.setPesoAdbAmount(adbPesoAmount);
            adb.setBookCode("RG");
            
            // set GLaccountID to '-' for BE
            if("TF119".equalsIgnoreCase(adb.getProductId()))
            {
            	adb.setGlAccountId("-");
            	adb.setOutstandingBalance(BigDecimal.ZERO);
            	adb.setAdbAmount(BigDecimal.ZERO);
            	adb.setPesoAdbAmount(BigDecimal.ZERO);
            }
            
            if(adb.getAllocationUnit()==null || adb.getAllocationUnit().isEmpty()){
            	adb.setAllocationUnit("58" + adb.getBranchUnitCode());
            	System.out.println("adb.getAllocationUnit() null:"+adb.getAllocationUnit());
                //income.setAllocationUnit(getMainOfficeBranch());
            }
            
            // After everything, set currency to PHP (as per SIBS)
            adb.setCurrencyId("PHP");
        }

        return activeProducts;
    }

    private String getAllocationUnitCode(String officerCode){
        System.out.println("officerCode:"+officerCode);
        String allocationUnitCode = null;
        if(StringUtils.isNumeric(officerCode)){
            allocationUnitCode = silverlakeLocalDao.getAllocationUnitCodeForNumericOfficerCode(officerCode);
        }else{
            allocationUnitCode = silverlakeLocalDao.getAllocationUnitCodeForAlphanumericOfficerCode(officerCode);
            if(allocationUnitCode == null){
                allocationUnitCode = silverlakeLocalDao.getAllocationUnitCodeForAlphaOfficerCode(officerCode);
            }
        }

        return allocationUnitCode;
    }


    public void setAllocationDao(AllocationDao allocationDao) {
        this.allocationDao = allocationDao;
    }

    public void setTradeProductDao(TradeProductDao tradeProductDao) {
        this.tradeProductDao = tradeProductDao;
    }

    public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao) {
        this.silverlakeLocalDao = silverlakeLocalDao;
    }

    public String getMainOfficeBranch() {
        return mainOfficeBranch != null ? mainOfficeBranch : CONTRA_UNIT;
    }

	@Override
	public List<AllocationFileRecord> getProductAllocationsException(Date currentDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode) {

        List<AllocationFileRecord> incomesWithSplit = new ArrayList<AllocationFileRecord>();

        List<AllocationFileRecord> incomes = allocationDao.getAllocationsException(currentDate);

        System.out.println("income size:"+incomes.size());

        for (AllocationFileRecord income : incomes) {

            System.out.println(income.getApplicationAccountId()+"-"+income.getProductId()+"-"+income.getEventTransactionId());
            System.out.println("income.getAllocationUnit():"+income.getAllocationUnit());

            if(income.getAllocationUnit()==null || income.getAllocationUnit().isEmpty()){
            	income.setAllocationUnit("-");
            	//income.setAllocationUnit("58" + income.getBranchUnitCode());
            	System.out.println("income.getAllocationUnit() null:"+income.getAllocationUnit());
            }
            String documentNumber = income.getApplicationAccountId();

            incomesWithSplit.add(income);
        }

        return incomesWithSplit;
	}

	@Override
	public List<AllocationFileRecord> getProductAverageDailyBalanceRecordsException(Date currentDate) {

        // LC
        List<AllocationFileRecord> activeProducts = allocationDao.getValidLetterOfCreditsException(currentDate);
        System.out.println("with LC size: " + activeProducts.size());

        // for adding a new record for LC Regular that is adjust to LC Cash
        List<AllocationFileRecord> modifiedLcProduct = new ArrayList<AllocationFileRecord>();
        for(AllocationFileRecord allocationFileRecord:activeProducts)
        {
        	if(allocationFileRecord.getDocumentSubType1().equals(DocumentSubType1.REGULAR) && allocationFileRecord.getCashFlag() == 1){        	       		
        		//for creating new record with document number for Adjusted Regular to Cash
                AllocationFileRecord tempAllocFileRecord = allocationFileRecord.getCloneAllocFileRecord();
            	StringBuilder tempAccountId = new StringBuilder(tempAllocFileRecord.getApplicationAccountId());
            	StringBuilder tempDocuNumber = new StringBuilder(tempAllocFileRecord.getDocumentNumber());
                if(tempAccountId.toString().startsWith("FX")){
                    tempAccountId.replace(4,6,"98");
                    tempDocuNumber.replace(4, 6, "98");
                    tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                    tempAllocFileRecord.setProductId("TF113");
                    tempAllocFileRecord.setGlAccountId("246210101000");
                }else if(tempAccountId.toString().startsWith("DM")){
                    tempAccountId.replace(4,6,"99");
                    tempDocuNumber.replace(4, 6, "99");
                    tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                    tempAllocFileRecord.setProductId("TF217");
                    tempAllocFileRecord.setGlAccountId("246210102000");
                }else
                {
                    if(tempAllocFileRecord.getDocumentType().equals(DocumentType.FOREIGN)) {
                        tempAccountId.replace(3, 5, "98");
                        tempDocuNumber.replace(4, 6, "98");
                        tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                        tempAllocFileRecord.setProductId("TF113");
                        tempAllocFileRecord.setGlAccountId("246210101000");
                    }else{
                        tempAccountId.replace(3, 5, "99");
                        tempDocuNumber.replace(4, 6, "99");
                        tempAllocFileRecord.setApplicationAccountId(tempAccountId.toString());
                        tempAllocFileRecord.setProductId("TF217");
                        tempAllocFileRecord.setGlAccountId("246210102000");
                    }
                }
                tempAllocFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(tempDocuNumber.toString(), currentDate));
                tempAllocFileRecord.setOutstandingBalance(tempAllocFileRecord.getCashAmount());
                tempAllocFileRecord.setOpenDate(allocationDao.getAdjustmentCashOpenDate(allocationFileRecord.getDocumentNumber()));
                modifiedLcProduct.add(tempAllocFileRecord);// add modified LC to modifiedLcProduct
                
                allocationFileRecord.setTotalAmount(allocationDao.getTotalDailyBalancePerDocument(allocationFileRecord.getDocumentNumber(), currentDate));
                allocationFileRecord.setDateClosed(allocationDao.getAdjustmentRegEndDate(allocationFileRecord.getDocumentNumber()));
            	allocationFileRecord.setGlAccountId(allocationFileRecord.getPreviousLiabilitiesGlNumber());
            	allocationFileRecord.setProductId(allocationFileRecord.getPreviousProductID());
            	
        	}

        }

        //add the trade product with new document number
        activeProducts.addAll(modifiedLcProduct);

        // DA
        activeProducts.addAll(allocationDao.getValidDocumentsAgainstAcceptanceException(currentDate));
        System.out.println("with DA size: " + activeProducts.size());

        // DP
        activeProducts.addAll(allocationDao.getValidDocumentsAgainstPaymentException(currentDate));
        System.out.println("with DP size: " + activeProducts.size());

        // BC
        activeProducts.addAll(allocationDao.getActiveBillsCollectionException(currentDate));
        System.out.println("with BC size: " + activeProducts.size());

        // Indemnity
        activeProducts.addAll(allocationDao.getActiveBankGuaranteeException(currentDate));
        System.out.println("with BG size: " + activeProducts.size());

        System.out.println("activeProducts size: " + activeProducts.size());

        for (AllocationFileRecord adb : activeProducts) {

            //COMPUTE PESO AMOUNT
            System.out.println("Total AMOUNT = " + adb.getTotalAmount());
            System.out.println("Original ABD AMOUNT = " + adb.getAdbAmount());
            System.out.println("Original CURRENCY = " + adb.getCurrencyId());

            BigDecimal adbPesoAmount = BigDecimal.ZERO;
            adbPesoAmount = adb.getAdbAmount();  // Already rounds HALF UP

            //Check ProductId if NON LC or Cash LC
            //If true reverse signs
            if ("A".equalsIgnoreCase(adb.getGlAccountType())) {
                adbPesoAmount = adbPesoAmount.negate();
            } 
            adb.setPesoAdbAmount(adbPesoAmount);
            adb.setBookCode("RG");
            
            // set GLaccountID to '-' for BE
            if("TF119".equalsIgnoreCase(adb.getProductId()))
            {
            	adb.setGlAccountId("-");
            	adb.setOutstandingBalance(BigDecimal.ZERO);
            	adb.setAdbAmount(BigDecimal.ZERO);
            	adb.setPesoAdbAmount(BigDecimal.ZERO);
            }
            
            if(adb.getAllocationUnit()==null || adb.getAllocationUnit().isEmpty()){
            	//adb.setAllocationUnit("58" + adb.getBranchUnitCode());
            	adb.setAllocationUnit("-");
            	System.out.println("adb.getAllocationUnit() null:"+adb.getAllocationUnit());
            }
            
            // After everything, set currency to PHP (as per SIBS)
            adb.setCurrencyId("PHP");
        }

        return activeProducts;
	}
}
