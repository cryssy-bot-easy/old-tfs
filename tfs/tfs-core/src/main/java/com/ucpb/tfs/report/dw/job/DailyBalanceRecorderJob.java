package com.ucpb.tfs.report.dw.job;


import com.ucpb.tfs.batch.dao.BatchProcessDao;
import com.ucpb.tfs.batch.job.SpringJob;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.util.DateUtil;
import com.ucpb.tfs.batch.report.dw.DailyBalanceRecord;
import com.ucpb.tfs.batch.report.dw.DocumentType;
import com.ucpb.tfs.domain.reference.DailyBalance;
import com.ucpb.tfs.domain.reference.DailyBalanceRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;


public class DailyBalanceRecorderJob implements SpringJob {

	/*	PROLOGUE:
		(revision)
		SCR/ER Number: 20151104-016
		SCR/ER Description: Daily Balance Program does not  include LCs  which were  amended or negotiated thru reinstatement and wrongly
		 					sets  to  negative regular balance for fully adjusted to cash LCs during decrease in amount amendments.
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 10/23/2015
		Program [Revision] Details: Added conditions which set zero for all negative balance of Regular LCs (partial/fully adjusted to Cash)
		 							before saving to database.
		Date deployment: 11/04/2015
		Member Type: JAVA
		Project: CORE
		Project Name: DailyBalanceRecorderJob.java
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
	Program [Revision] Details: 1.  Added condition to create a new document number with “88” for FOREIGN and “89” for DOMESTIC when the TENOR is
	 								changed from SIGHT to USANCE, indicating its corresponding daily balance. 
								2.  Added condition to create a new document number with “78” for FOREIGN and “79” for DOMESTIC when the STANDBY TAGGING
								 	is changed  from PERFORMANCE to FINANCIAL or vice versa, , indicating its corresponding daily balance.
	Date deployment: 
	Member Type: JAVA
	Project: CORE
	Project Name: DailyBalanceRecorderJob.java	
	*/

	private static final String DATE_FORMAT = "MMddyy";
	
    private BatchProcessDao batchProcessDao;
    
    private SilverlakeLocalDao silverlakeLocalDao;

    @Autowired
    private DailyBalanceRepository dailyBalanceRepository;
    
    @Override
    public void execute() throws Exception {
        batchProcessDao.deleteDailyBalance(new java.util.Date());
        batchProcessDao.insertToDailyBalance(new java.util.Date());
    }

    @Override
    public void execute(String reportDate) throws Exception {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date runDate = dateFormat.parse(reportDate);
        batchProcessDao.deleteDailyBalance(runDate);
        //batchProcessDao.insertToDailyBalance(runDate);
        List<DailyBalanceRecord> allDailyBalance = batchProcessDao.getOriginalDailyBalance(runDate);
        System.out.println("allDailyBalance size:"+allDailyBalance.size());
        
        
        for (DailyBalanceRecord dailyBalance : allDailyBalance) {
        	String documentNumber = dailyBalance.getDocumentNumber();
        	Date balanceDate =  dailyBalance.getBalanceDate();
        	BigDecimal balance = BigDecimal.ZERO;
        	BigDecimal revalRate = BigDecimal.ZERO;
        	BigDecimal originalBalance = dailyBalance.getOriginalBalance();
        	String currency = dailyBalance.getCurrency().trim();
        	//DailyBalance dBalanceRecord = new DailyBalance();
        	DailyBalanceRecord dBalanceRecord = new DailyBalanceRecord();
        	
        	if(dailyBalance.getProductType().trim().equalsIgnoreCase("LC")){
        		if(dailyBalance.getCashFlag() != null && dailyBalance.getLcType().trim().equalsIgnoreCase("REGULAR") && dailyBalance.getCashFlag() == 1){
        			revalRate = getRevalRate(currency, balanceDate);
        			
        			//For Cash
        			String newDocumentNumber = documentNumber;
        			BigDecimal cashAmount = (BigDecimal) dailyBalance.getCashAmount();
        			BigDecimal totalNegotiatedCashAmount = (BigDecimal) dailyBalance.getTotalNegotiatedCashAmount();
        			BigDecimal cashBalance = cashAmount.subtract(totalNegotiatedCashAmount);
        			
        			if(cashBalance.compareTo(BigDecimal.ZERO) == 1){
	        			balance = convertBalance(cashBalance, currency, balanceDate);
	        			if(dailyBalance.getDocumentType().trim().equalsIgnoreCase("FOREIGN")){
	        				newDocumentNumber = documentNumber.substring(0, 4) + "98" + documentNumber.substring(6);
	        				dBalanceRecord.setProductId("TF113");
	        			} else {
	        				newDocumentNumber = documentNumber.substring(0, 4) + "99" + documentNumber.substring(6);
	        				dBalanceRecord.setProductId("TF217");
	        			}
	        			dBalanceRecord.setDocumentNumber(newDocumentNumber);
	        			dBalanceRecord.setBalance(balance);
	        			dBalanceRecord.setBalanceDate(balanceDate);
	        			dBalanceRecord.setOriginalBalance(cashBalance);
	        			dBalanceRecord.setRevalRate(revalRate);
	        			dBalanceRecord.setCurrency(currency);
	        			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
	        			System.out.println("documentNumber:" + newDocumentNumber + " balance:" + balance.toPlainString() + " balanceDate:" + balanceDate + " originalBalance:" + cashBalance + " revalRate:" + revalRate + " currency:" + currency);                	     			
        			}
        			
        			//For Regular
        			if(originalBalance.compareTo(BigDecimal.ZERO) == 1){
	        			dBalanceRecord = new DailyBalanceRecord();
	        			BigDecimal regularBalance = originalBalance.subtract(cashBalance);       			
	        			balance = convertBalance(regularBalance, currency, balanceDate);
	        			if(regularBalance.compareTo(BigDecimal.ZERO) == -1){
	        				regularBalance = BigDecimal.ZERO;
	        				balance = BigDecimal.ZERO;
	        			}
	        			dBalanceRecord.setDocumentNumber(documentNumber);
	        			dBalanceRecord.setBalance(balance);
	        			dBalanceRecord.setBalanceDate(balanceDate);
	        			dBalanceRecord.setOriginalBalance(regularBalance);
	        			dBalanceRecord.setRevalRate(revalRate);
	        			dBalanceRecord.setCurrency(currency);
	        			dBalanceRecord.setProductId(dailyBalance.getProductId());
	        			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
	        			System.out.println("documentNumber:" + documentNumber + " balance:" + balance + " balanceDate:" + balanceDate + " originalBalance:" + regularBalance + " revalRate:" + revalRate + " currency:" + currency);
        			}
        		} else if(dailyBalance.getCashFlag() != null && dailyBalance.getLcType().trim().equalsIgnoreCase("CASH") && dailyBalance.getCashFlag() == 1){
        			System.out.println("CASHdocumentNumber:" + documentNumber);
        			revalRate = getRevalRate(currency, balanceDate);
        			
        			List<Map<String, Integer>> allCashFlags = batchProcessDao.getCashFlags(documentNumber);
        			boolean lcAdjusted = false;
        			
        			System.out.println("allCashFlags" + allCashFlags);
        			        			
        			for(Map<String, Integer> cashFlag : allCashFlags){
        				if(cashFlag == null || cashFlag.get("CASHFLAG") == 0 || cashFlag.get("CASHFLAG").toString() == ""){
        					lcAdjusted = true;
        				}
        			}
        			
        			//For Cash
        			String newDocumentNumber = documentNumber;
        			BigDecimal cashAmount = (BigDecimal) dailyBalance.getCashAmount();
        			BigDecimal totalNegotiatedCashAmount = (BigDecimal) dailyBalance.getTotalNegotiatedCashAmount();
        			BigDecimal cashBalance = cashAmount.subtract(totalNegotiatedCashAmount);
        			
        			if(cashBalance.compareTo(BigDecimal.ZERO) == 1){
	        			balance = convertBalance(cashBalance, currency, balanceDate);
	        			if(lcAdjusted){
	        				if(dailyBalance.getDocumentType().trim().equalsIgnoreCase("FOREIGN")){
	        					newDocumentNumber = documentNumber.substring(0, 4) + "98" + documentNumber.substring(6);
	        					dBalanceRecord.setProductId("TF113");
	        				} else {
	        					newDocumentNumber = documentNumber.substring(0, 4) + "99" + documentNumber.substring(6);
	        					dBalanceRecord.setProductId("TF217");
	        				}
	        			} else {
	        				dBalanceRecord.setProductId(dailyBalance.getProductId());
	        			}
	        			dBalanceRecord.setDocumentNumber(newDocumentNumber);
	        			dBalanceRecord.setBalance(balance);
	        			dBalanceRecord.setBalanceDate(balanceDate);
	        			dBalanceRecord.setOriginalBalance(cashBalance);
	        			dBalanceRecord.setRevalRate(revalRate);
	        			dBalanceRecord.setCurrency(currency);
	        			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
	        			System.out.println("documentNumber:" + newDocumentNumber + " balance:" + balance.toPlainString() + " balanceDate:" + balanceDate + " originalBalance:" + cashBalance + " revalRate:" + revalRate + " currency:" + currency);
	        		}
        		} else if (batchProcessDao.checkIfTenorChange(documentNumber, runDate) != null && dailyBalance.getLcType().trim().equalsIgnoreCase("REGULAR") && batchProcessDao.checkIfTenorChange(documentNumber, runDate).equalsIgnoreCase("Y")){
        			//for creating new record with document number for Change of Tenor from Regular Sight to Usance
        			if(originalBalance.compareTo(BigDecimal.ZERO) == 1){
	            		revalRate = getRevalRate(currency, balanceDate);
	            		balance = convertBalance(originalBalance, currency, balanceDate);
	            		String newDocumentNumber = documentNumber;
	            		if(dailyBalance.getDocumentType().trim().equalsIgnoreCase("FOREIGN")){
        					newDocumentNumber = documentNumber.substring(0, 4) + "88" + documentNumber.substring(6);
        					dBalanceRecord.setProductId("TF112");
        				} else {
        					newDocumentNumber = documentNumber.substring(0, 4) + "89" + documentNumber.substring(6);
        					dBalanceRecord.setProductId("TF212");
        				}
	            		dBalanceRecord.setDocumentNumber(newDocumentNumber);
	        			dBalanceRecord.setBalance(balance);
	        			dBalanceRecord.setBalanceDate(balanceDate);
	        			dBalanceRecord.setOriginalBalance(originalBalance);
	        			dBalanceRecord.setRevalRate(revalRate);
	        			dBalanceRecord.setCurrency(currency);
	        			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
	        			System.out.println("documentNumber:" + newDocumentNumber + " balance:" + balance + " balanceDate:" + balanceDate + " originalBalance:" + originalBalance + " revalRate:" + revalRate + " currency:" + currency);
        			}
        		} else if(dailyBalance.getLcType().trim().equalsIgnoreCase("STANDBY") && originalBalance.compareTo(BigDecimal.ZERO) == 1){
        			List<Integer> revIdList = batchProcessDao.getAuditRevId(dailyBalance.getDocumentNumber(), runDate);
            		Boolean isStandbyTaggingAdjusted = false;
            		String lastStandbyTagging = null;
            		revalRate = getRevalRate(currency, balanceDate);
            		balance = convertBalance(originalBalance, currency, balanceDate);
            		for(Integer revId:revIdList){
            			System.out.println("----STANDBY---- DocNum: " + dailyBalance.getDocumentNumber());
            			List<DailyBalanceRecord> letterOfCreditAuditList = batchProcessDao.getLetterOfCreditAuditByRevId(revId);
            			
            			Iterator<DailyBalanceRecord> letterOfCreditAuditDetails = letterOfCreditAuditList.iterator();
            			while(letterOfCreditAuditDetails.hasNext() && isStandbyTaggingAdjusted == false){
            				DailyBalanceRecord letterOfCreditAudit = letterOfCreditAuditDetails.next();
            				String standbyTagging = letterOfCreditAudit.getStandbyTagging();
            				
            				if(lastStandbyTagging != null && !lastStandbyTagging.equalsIgnoreCase(standbyTagging)){
            					isStandbyTaggingAdjusted = true;
            				} else {
            					lastStandbyTagging = standbyTagging;
            				}
            				System.out.println("isStandbyTaggingAdjusted: " + isStandbyTaggingAdjusted);
            				
            				if(isStandbyTaggingAdjusted){
            					//for creating new record with document number for Adjustment of Standby Tagging from Performance to Financial or Financial to Performance
        	            		String newDocumentNumber = documentNumber;
        	            		String openingStandbyTagging = batchProcessDao.getOpeningStandbyTagging(dailyBalance.getDocumentNumber());
           
        	            		if(lastStandbyTagging.equalsIgnoreCase(openingStandbyTagging)){
        	            			dBalanceRecord.setDocumentNumber(documentNumber);
        	            			dBalanceRecord.setProductId(dailyBalance.getProductId());
        	            		} else {
        	            			if(openingStandbyTagging.equalsIgnoreCase("PERFORMANCE")){       		
                	            		if(documentNumber.startsWith("FX") || dailyBalance.getDocumentType().trim().equalsIgnoreCase("FOREIGN")){
                	            			newDocumentNumber = documentNumber.substring(0, 4) + "78" + documentNumber.substring(6);
                        					dBalanceRecord.setProductId("TF115");
                	            		} else {
                	            			newDocumentNumber = documentNumber.substring(0, 4) + "79" + documentNumber.substring(6);
                        					dBalanceRecord.setProductId("TF213");
                	            		}

                	            	} else if(openingStandbyTagging.equalsIgnoreCase("FINANCIAL")){
                	            		if(documentNumber.startsWith("FX") || dailyBalance.getDocumentType().trim().equalsIgnoreCase("FOREIGN")){
                	            			newDocumentNumber = documentNumber.substring(0, 4) + "78" + documentNumber.substring(6);
                        					dBalanceRecord.setProductId("TF116");
                	            		} else {
                	            			newDocumentNumber = documentNumber.substring(0, 4) + "79" + documentNumber.substring(6);
                        					dBalanceRecord.setProductId("TF214");
                	            		}
                	            	}
        	            			dBalanceRecord.setDocumentNumber(newDocumentNumber);
        	            		}
        	        			dBalanceRecord.setBalance(balance);
        	        			dBalanceRecord.setBalanceDate(balanceDate);
        	        			dBalanceRecord.setOriginalBalance(originalBalance);
        	        			dBalanceRecord.setRevalRate(revalRate);
        	        			dBalanceRecord.setCurrency(currency);
        	        			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
        	        			System.out.println("documentNumber:" + documentNumber + " balance:" + balance + " balanceDate:" + balanceDate + " originalBalance:" + originalBalance + " revalRate:" + revalRate + " currency:" + currency);
            				}
            			}
            		}
            		
            		if(isStandbyTaggingAdjusted == false){
	            		dBalanceRecord.setDocumentNumber(documentNumber);
	        			dBalanceRecord.setBalance(balance);
	        			dBalanceRecord.setBalanceDate(balanceDate);
	        			dBalanceRecord.setOriginalBalance(originalBalance);
	        			dBalanceRecord.setRevalRate(revalRate);
	        			dBalanceRecord.setCurrency(currency);
	        			dBalanceRecord.setProductId(dailyBalance.getProductId());
	        			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
	        			System.out.println("documentNumber:" + documentNumber + " balance:" + balance + " balanceDate:" + balanceDate + " originalBalance:" + originalBalance + " revalRate:" + revalRate + " currency:" + currency);            			
            		}
            	} else {
        			if(originalBalance.compareTo(BigDecimal.ZERO) == 1){
	            		revalRate = getRevalRate(currency, balanceDate);
	            		balance = convertBalance(originalBalance, currency, balanceDate);
	            		dBalanceRecord.setDocumentNumber(documentNumber);
	        			dBalanceRecord.setBalance(balance);
	        			dBalanceRecord.setBalanceDate(balanceDate);
	        			dBalanceRecord.setOriginalBalance(originalBalance);
	        			dBalanceRecord.setRevalRate(revalRate);
	        			dBalanceRecord.setCurrency(currency);
	        			dBalanceRecord.setProductId(dailyBalance.getProductId());
	        			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
	        			System.out.println("documentNumber:" + documentNumber + " balance:" + balance + " balanceDate:" + balanceDate + " originalBalance:" + originalBalance + " revalRate:" + revalRate + " currency:" + currency);
        			}
            	}
        	} else {
        		if(originalBalance.compareTo(BigDecimal.ZERO) == 1){
	        		revalRate = getRevalRate(currency, balanceDate);
	        		balance = convertBalance(originalBalance, currency, balanceDate);
	        		dBalanceRecord.setDocumentNumber(documentNumber);
	    			dBalanceRecord.setBalance(balance);
	    			dBalanceRecord.setBalanceDate(balanceDate);
	    			dBalanceRecord.setOriginalBalance(originalBalance);
	    			dBalanceRecord.setRevalRate(revalRate);
	    			dBalanceRecord.setCurrency(currency);
	    			dBalanceRecord.setProductId(dailyBalance.getProductId());
	    			batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
	    			System.out.println("documentNumber:" + documentNumber + " balance:" + balance + " balanceDate:" + balanceDate + " originalBalance:" + originalBalance + " revalRate:" + revalRate + " currency:" + currency);
        		}
        	}
        	
        	//dailyBalanceRepository.save(dBalanceRecord);
        	
        	//batchProcessDao.insertToDailyBalanceNew(dBalanceRecord);
         } 
    }
    
    private BigDecimal convertBalance(BigDecimal origBalance, String origCurrency, Date runDate) throws Exception{
    	BigDecimal balance = null;
    	BigDecimal revalRate = null;

    	if(origCurrency.equalsIgnoreCase("PHP")){
    		balance = origBalance;
    	} else {
    		System.out.println("System Date:" + getSystemDate());
    		System.out.println("Run Date:" + runDate);
    		if(runDate.equals(getSystemDate())){
    			//revalRate = silverlakeLocalDao.getAngolConversionRateForDailyBalance(origCurrency, "PHP", 18, DateUtil.formatToInt(DATE_FORMAT, runDate));
    			revalRate = silverlakeLocalDao.getAngolConversionRate(origCurrency, "PHP", 18); 
    				if (revalRate == null ) {

		                 // If null, then get from historical rates
		
		                 // Below is for U2; hard-coded to Feb. 28, 2014 rates
		                 // Calendar calendar = Calendar.getInstance();
		                 // calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		                 // calendar.set(Calendar.DATE, 28);
		                 // calendar.set(Calendar.YEAR, 2014);
		                 // Date historicalDate = calendar.getTime();
		
		                 Date historicalDate = runDate;
		                 revalRate = silverlakeLocalDao.getHistoricalRevalRate(DateUtil.formatToInt(DATE_FORMAT, historicalDate), origCurrency, "PHP", 18);
		              }
    		} else {
    			Date historicalDate = runDate;
                revalRate = silverlakeLocalDao.getHistoricalRevalRate(DateUtil.formatToInt(DATE_FORMAT, historicalDate), origCurrency, "PHP", 18);            
    		}
    		
            if (revalRate != null) {
            	balance = origBalance.multiply(revalRate); // Do not Round-off
            } else {
                System.out.println(">>>>> NO REVAL RATE OBTAINED FOR CURRENCY = " + origCurrency + ", date = " + DateUtil.formatToInt(DATE_FORMAT, runDate));
            }
    	}    	
		return balance;
    }
    
    private BigDecimal getRevalRate(String origCurrency, Date runDate) throws Exception{
    	BigDecimal revalRate = BigDecimal.ZERO;

    	if(!origCurrency.equalsIgnoreCase("PHP")){
    		if(runDate.equals(getSystemDate())){        		
    			//revalRate = silverlakeLocalDao.getAngolConversionRateForDailyBalance(origCurrency, "PHP", 18, DateUtil.formatToInt(DATE_FORMAT, runDate));
    			revalRate = silverlakeLocalDao.getAngolConversionRate(origCurrency, "PHP", 18); 
    			if (revalRate == null ) {

	                 // If null, then get from historical rates
	
	                 // Below is for U2; hard-coded to Feb. 28, 2014 rates
	                 // Calendar calendar = Calendar.getInstance();
	                 // calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
	                 // calendar.set(Calendar.DATE, 28);
	                 // calendar.set(Calendar.YEAR, 2014);
	                 // Date historicalDate = calendar.getTime();
	
	                 Date historicalDate = runDate;
	                 revalRate = silverlakeLocalDao.getHistoricalRevalRate(DateUtil.formatToInt(DATE_FORMAT, historicalDate), origCurrency, "PHP", 18);
	             }
    		} else {
    			Date historicalDate = runDate;
                revalRate = silverlakeLocalDao.getHistoricalRevalRate(DateUtil.formatToInt(DATE_FORMAT, historicalDate), origCurrency, "PHP", 18);            
    		}
    	}
		return revalRate;
    }
    
    private Date getSystemDate() throws Exception{
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    	Date today = new Date();
        String todayString = dateFormat.format(today);
        
        //parse systemDate to runDate format
        Date systemDate = dateFormat.parse(todayString);
		return systemDate;
    	
    }

    public void execute(java.util.Date serverDate) {
        System.out.println("public void execute(java.util.Date serverDate)");
        batchProcessDao.deleteDailyBalance(serverDate);
        batchProcessDao.insertToDailyBalance(serverDate);
    }

    public void setBatchProcessDao(BatchProcessDao batchProcessDao) {
        this.batchProcessDao = batchProcessDao;
    }
    
    public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao) {
        this.silverlakeLocalDao = silverlakeLocalDao;
    }
}


//@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
//public class DailyBalanceRecorderJob<T> implements SpringJob {
//
//    private final String query ;
//
//    private final DataSource dataSource;
//
//    private BeanRowMapper<DailyBalance> beanMapper;
//
//    private DailyBalanceRepository repository;
//
//
//    public DailyBalanceRecorderJob(String query,DataSource dataSource){
//        this.query = query;
//        this.dataSource = dataSource;
//    }
//
//    public void execute(){
//        System.out.println("execute");
//        System.out.println("sql:"+ query);
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//            Statement statement = connection.createStatement();
//            statement.execute(query);
//            statement.close();
////            PreparedStatement ps = connection.prepareStatement(query);
////            ResultSet rs = ps.executeQuery();
////            while(rs.next()){
////                DailyBalance balance = beanMapper.mapRow(rs, 0);
////                repository.save(balance);
////            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Database exception",e);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally{
//            DbUtil.closeQuietly(connection);
//        }
//
//    }
//
//    public void setBeanMapper(BeanRowMapper<DailyBalance> beanMapper) {
//        this.beanMapper = beanMapper;
//    }
//
//    public void setRepository(DailyBalanceRepository repository) {
//        this.repository = repository;
//    }
//}


