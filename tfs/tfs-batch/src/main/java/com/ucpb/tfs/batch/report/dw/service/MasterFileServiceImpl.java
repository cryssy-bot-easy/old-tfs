package com.ucpb.tfs.batch.report.dw.service;

import com.ucpb.tfs.batch.report.dw.*;
import com.ucpb.tfs.batch.util.TimeIgnoringDateComparator;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;

import java.math.BigDecimal;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 */
public class MasterFileServiceImpl implements MasterFileService {
	
	/*	PROLOGUE:
		(revision)
		SCR/ER Number: 20151104-015
		SCR/ER Description: Wrong Closed date and status  of Regular LC part for all fully adjusted to Cash in Master.
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 10/23/2015
		Program [Revision] Details: Added conditions which compared NegotiationDate, LastAmendmentDate, LastReinstatementDate and MaturityDate
									to set the correct status(TF-A, TF-C, TF-D, or TF-B) on the record in the Master File.
									If the record was partial/fully adjusted to cash and the regular amount part was zero, it called the
									function getAuditRevId() and checked if the record was fully adjusted to cash. It then retrieved the
									LastModifiedDate in LetterOfCredit_Audit of the record when it was fully adjusted to cash and set the
									LastModifiedDate as ClosedDate of the record. 
		Date deployment: 11/04/2015
		Member Type: JAVA
		Project: CORE
		Project Name: MasterFileServiceImpl.java
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
	 								changed from SIGHT to USANCE, indicating its corresponding outstanding amount. 
								2.  Added condition to create a new document number with “78” for FOREIGN and “79” for DOMESTIC when the STANDBY TAGGING
								 	is changed  from PERFORMANCE to FINANCIAL or vice versa, , indicating its corresponding  outstanding amount.
	Date deployment: 
	Member Type: JAVA
	Project: CORE
	Project Name: MasterFileServiceImpl.java	
	*/
	
	/**
	 * PROLOGUE
	 * SCR/ER Description: To correct the outstanding balance of EBC accounts with EBP.
	 *	[Revised by:] Jesse James Joson
	 *	Program [Revision] Details: Comment out the portion that set the balances to zero when EBC have an EBP.
	 *	Date deployment: 6/16/2016 
	Member Type: JAVA
	Project: CORE
	Project Name: MasterFileServiceImpl.java	
	*/

	/**
	 * PROLOGUE
	 * 
	 * Description: Replaced usage of silverlakeDao into silverlakeLocalDao
	 * Revised by:  Cedrick C. Nungay
	 * Date revised:01/25/2024
	*/
	
    private static final String SHORT_TERM = "1";
	private static final String LONG_TERM = "2";
    public static final String ASSET = "A";
    public static final String LIABILITY = "L";
    public static final String BOOKCODE = "BOOKCODE";
    public static final String ACCOUNTINGCODE = "ACCOUNTINGCODE";
    private SilverlakeLocalDao silverlakeLocalDao;
    private TradeProductDao tradeProductDao;
    private static final TimeIgnoringDateComparator DATE_COMPARATOR = new TimeIgnoringDateComparator();


//    @Override
//    public List<MasterFileRecord> getMasterFiles() {
//        System.out.println("getMasterFiles");
//
//        List<TradeProduct> activeProducts = getAllActiveProducts();
//        List<MasterFileRecord> masterFileRecords = new ArrayList<MasterFileRecord>();
//        for(TradeProduct tradeProduct : activeProducts){
//
//        	MasterFileRecord record = new MasterFileRecord(tradeProduct);
//            record.setProductId(tradeProduct.getProductId());
//            record.setIndustryCode(silverlakeLocalDao.getIndustryCode(record.getCustomerId()));
//
//            Map<String,String> creationContingentAsset = getContingentByTradeServiceIdAndType(tradeProduct.getCreationTradeServiceId(), ASSET);
//            Map<String,String> creationContingentLiability = getContingentByTradeServiceIdAndType(tradeProduct.getCreationTradeServiceId(), LIABILITY);
//
//            record.setOutstandingBookCode("RG");
//            record.setContingentLiabilitiesGlNumber(creationContingentLiability.get(ACCOUNTINGCODE));
//            record.setContingentAssetsGlNumber(creationContingentAsset.get(ACCOUNTINGCODE));
//
//            if(DocumentClass.LC.equals(tradeProduct.getDocumentClass()) && DocumentSubType1.REGULAR.equals(tradeProduct.getDocumentSubType1())){
//                record.setAppraisal(silverlakeLocalDao.getAppraisalDetails(tradeProduct.getMainCifNumber()));
//                Appraisal appraisal = silverlakeLocalDao.getAppraisalDetails(tradeProduct.getMainCifNumber());
//            }
//
////            System.out.println(""+tradeProduct.getDocumentClass() +" = "+tradeProduct.getDocumentSubType1() + " = " + tradeProduct.getDocumentSubType2());
//            if("USANCE".equalsIgnoreCase(tradeProduct.getDocumentSubType2())){
//                System.out.println("USANCE");
//            }
//
//            record.setModeOfPayment(tradeProduct.getDocumentClass().getModeOfPayment());
//
//            //TODO UA MATURITY
//
//        	record.setCreditFacilityCode(generateCreditFacilityCode(tradeProduct.getMaturityDate(), tradeProduct.getOpenDate()));
//        	masterFileRecords.add(record);
//            System.out.println(record);
//        }
//
//        return masterFileRecords;
//    }

    @Override
    public List<MasterFileRecord> getMasterFiles(String appDate) {

        System.out.println("getMasterFiles");

        List<TradeProduct> activeProducts = getAllActiveProducts(appDate);
        
        //set outstanding balance to 0 of EBC for EBP with prior EBC
//        for(TradeProduct tempTradeProduct:activeProducts){
//            if("TF513".equalsIgnoreCase(tempTradeProduct.getProductId()) && tempTradeProduct.getNegoNumber()!= null){
//            	for(TradeProduct temp00:activeProducts){
//            		if(tempTradeProduct.getNegoNumber().equalsIgnoreCase(temp00.getApplicationAccountId())){
//            			temp00.setOutstandingContingentAssets(BigDecimal.ZERO);
//            			temp00.setOutstandingContingentLiabilities(BigDecimal.ZERO);
//            			break;
//            		}
//            	}        	         	
//            }
//        }
        
        List<MasterFileRecord> masterFileRecords = new ArrayList<MasterFileRecord>();
        for(TradeProduct tradeProduct : activeProducts){

            try{

                MasterFileRecord record = new MasterFileRecord(tradeProduct);

                record.setProductId(tradeProduct.getProductId());

                // record.setIndustryCode(silverlakeLocalDao.getIndustryCode(record.getCustomerId())); //COMMENT OUT FOR LOCAL

//            Map<String,String> creationContingentAsset = getContingentByTradeServiceIdAndType(tradeProduct.getCreationTradeServiceId(), ASSET);
//            Map<String,String> creationContingentLiability = getContingentByTradeServiceIdAndType(tradeProduct.getCreationTradeServiceId(), LIABILITY);

                record.setOutstandingBookCode("RG");
//            if(!creationContingentAsset.isEmpty()){
//                record.setContingentAssetsGlNumber(creationContingentAsset.get(ACCOUNTINGCODE));
//            }
//            if(!creationContingentLiability.isEmpty()){
//                record.setContingentLiabilitiesGlNumber(creationContingentLiability.get(ACCOUNTINGCODE));
//            }

                if ("TF217".equalsIgnoreCase(record.getProductId())||"TF113".equalsIgnoreCase(record.getProductId())) {
                	//set ContingentLiabilitiesGlNumber to ContingentAssetsGlNumber
                    System.out.println("CASH LC ContingentLiabilitiesGlNumber to ContingentAssetsGlNumber");
                    record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
                    record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                }
                
                System.out.println("tradeProduct.getGlAccountType():"+tradeProduct.getGlAccountType());

                if("L".equals(tradeProduct.getGlAccountType())){
                    //Handles Amounts
                    //reverse the signs
                    if(!"-".equals(tradeProduct.getContingentAssetsGlNumber()) && tradeProduct.getOutstandingContingentAssets()!=null){
                       System.out.println("ContingentAssets");
                       System.out.println("tradeProduct.getOutstandingContingentAssets():"+tradeProduct.getOutstandingContingentAssets());
                       System.out.println("tradeProduct.getOutstandingContingentAssets() NEGATE:"+tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).negate());
                        record.setOutstandingContingentAssets(tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).negate());
                    }else{
                    	record.setOutstandingContingentAssets(BigDecimal.ZERO);
                    }
                    if(!"-".equals(tradeProduct.getContingentLiabilitiesGlNumber()) && tradeProduct.getOutstandingContingentLiabilities()!=null){
                     	System.out.println("ContingentLiabilities");
                     	System.out.println("tradeProduct.getOutstandingContingentLiabilities():"+tradeProduct.getOutstandingContingentLiabilities());
                     	System.out.println("tradeProduct.getOutstandingContingentLiabilities() NEGATE:"+tradeProduct.getOutstandingContingentLiabilities().setScale(2, BigDecimal.ROUND_FLOOR).negate());
                        record.setOutstandingContingentLiabilities(tradeProduct.getOutstandingContingentLiabilities().setScale(2, BigDecimal.ROUND_FLOOR));
                    }else{
                    	record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                    }
                }

                if("A".equals(tradeProduct.getGlAccountType())){
                     System.out.println("Swapping A");
                    //Handles GL Account Number
                    String tempStringCL =tradeProduct.getContingentLiabilitiesGlNumber();
                    String tempStringCA =tradeProduct.getContingentAssetsGlNumber();
                    record.setContingentAssetsGlNumber(tempStringCL);
                    record.setContingentLiabilitiesGlNumber(tempStringCA);

//                if(tradeProduct.getContingentLiabilitiesGlNumber()!=null){
//                    record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
//                }
//                if(tradeProduct.getContingentAssetsGlNumber()!=null){
//                    record.setContingentLiabilitiesGlNumber(tradeProduct.getContingentAssetsGlNumber());
//                }
                }

                // LC Cash
                if ("TF217".equalsIgnoreCase(record.getProductId())||"TF113".equalsIgnoreCase(record.getProductId())) {
                     System.out.println("CASH LC HERE");
                    //Handles Amounts
                    //reverse the signs
//                     record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
//                     record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
//                    if(!"-".equals(tradeProduct.getContingentAssetsGlNumber()) && tradeProduct.getOutstandingContingentAssets()!=null){
//                          System.out.println("ContingentAssets");
//                         System.out.println("tradeProduct.getOutstandingContingentAssets():"+tradeProduct.getOutstandingContingentAssets());
//                          System.out.println("tradeProduct.getOutstandingContingentAssets() NEGATE:"+tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).negate());
//                        record.setOutstandingContingentAssets(tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR));
//                    }
//                    else{
//                    	record.setOutstandingContingentAssets(BigDecimal.ZERO);
//                    }
                    
//                    if(!"-".equals(tradeProduct.getContingentLiabilitiesGlNumber()) && tradeProduct.getOutstandingContingentLiabilities()!=null){
//                         System.out.println("ContingentLiabilities");
//                         System.out.println("tradeProduct.getOutstandingContingentLiabilities():"+tradeProduct.getOutstandingContingentLiabilities());
//                        record.setOutstandingContingentLiabilities(tradeProduct.getOutstandingContingentLiabilities().setScale(2, BigDecimal.ROUND_FLOOR));
//                        System.out.println("tradeProduct.getOutstandingContingentLiabilities():"+tradeProduct.getOutstandingContingentLiabilities());
//                    }else{
//                    	record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
//                    }
                    
                    //Handles GL Account Number Reversal
                                       System.out.println("tradeProduct.getContingentLiabilitiesGlNumber()"+tradeProduct.getContingentLiabilitiesGlNumber());
                     System.out.println("tradeProduct.getContingentAssetsGlNumber()"+tradeProduct.getContingentAssetsGlNumber());
/*                    String tempStringCL =tradeProduct.getContingentLiabilitiesGlNumber();
                    String tempStringCA =tradeProduct.getContingentAssetsGlNumber();
                    record.setContingentAssetsGlNumber(tempStringCL);
                    record.setContingentLiabilitiesGlNumber(tempStringCA);
*/
//                if(tradeProduct.getContingentLiabilitiesGlNumber()!=null){
//                	record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
//                }
//                if(tradeProduct.getContingentAssetsGlNumber()!=null){
//                	record.setContingentLiabilitiesGlNumber(tradeProduct.getContingentAssetsGlNumber());
//                }

//                Calendar cal = Calendar.getInstance();
//                Date tempDate = cal.getTime();
                    Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
                    if(tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.ZERO) == 0 &&
                            !"TF-A".equalsIgnoreCase(record.getAccountStatusId())){
                    	record.setAccountStatusId("TF-B");
                    	tradeProduct.setAccountStatusId("CLOSED");
                    } else if(record.getMaturityDate() != null &&
                            DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()) >= 0 &&
                            !"TF-A".equalsIgnoreCase(record.getAccountStatusId()) && 
                            !"TF-B".equalsIgnoreCase(record.getAccountStatusId())){ 
                    	if(DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()) == 0){
                    		record.setAccountStatusId("TF-C");
                        	tradeProduct.setAccountStatusId("CURRENT");
                    	} else if(record.getLastReinstatementDate() != null && record.getNegotiationDate() != null){
                			if(DATE_COMPARATOR.compare(tradeProduct.getLastReinstatementDate(), tradeProduct.getNegotiationDate()) >= 0){
	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
	                        		record.setAccountStatusId("TF-C");
	                            	tradeProduct.setAccountStatusId("CURRENT");
	                        	} else {
	                        		record.setAccountStatusId("TF-D");
	                            	tradeProduct.setAccountStatusId("MATURED");
	                        	}	                    		
                			} else if(DATE_COMPARATOR.compare(tradeProduct.getNegotiationDate(), tradeProduct.getLastReinstatementDate()) > 0){
	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) == 0){
	                        		record.setAccountStatusId("TF-C");
	                            	tradeProduct.setAccountStatusId("CURRENT");
	                        	} else {
	                        		record.setAccountStatusId("TF-D");
	                            	tradeProduct.setAccountStatusId("MATURED");
	                        	}
                			}
                		} else if(record.getLastReinstatementDate() != null){
                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
                        		record.setAccountStatusId("TF-C");
                            	tradeProduct.setAccountStatusId("CURRENT");
                        	} else {
                        		record.setAccountStatusId("TF-D");
                            	tradeProduct.setAccountStatusId("MATURED");
                        	}
                    	} else {
                    		record.setAccountStatusId("TF-D");
                        	tradeProduct.setAccountStatusId("MATURED");
                    	}
                    }


                } else {

                    // Any product that is NOT LC Cash
                	
                	if(DocumentClass.LC.equals(tradeProduct.getDocumentClass())){
                		if(("EXPIRED".equalsIgnoreCase(tradeProduct.getAccountStatusId())) && tradeProduct.getNegotiationDate() != null) {
        					try {						
        						Date appDate2 = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
        						
        						SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        						String negoDateString = dateFormat.format(tradeProduct.getNegotiationDate());
        						String masterDateString =dateFormat.format(appDate2);
        				        Date negoDate = dateFormat.parse(negoDateString);
        				        Date masterDate = dateFormat.parse(masterDateString);
        				        
        				        System.out.println("masterDate" + masterDate);
        				        System.out.println("negoDate" + negoDate);
        				        
        						if(masterDate.equals(negoDate)){
        							tradeProduct.setAccountStatusId("CURRENT");
        						}
        					} catch (ParseException e) {
        						System.out.println();
        						e.printStackTrace();
        					}
                    	} else if(record.getMaturityDate() != null && !"TF-A".equalsIgnoreCase(record.getAccountStatusId())
                    			&& !"TF-B".equalsIgnoreCase(record.getAccountStatusId())){
                    		Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
                    		if(record.getLastReinstatementDate() != null && record.getNegotiationDate() != null){
                    			if(DATE_COMPARATOR.compare(tradeProduct.getLastReinstatementDate(), tradeProduct.getNegotiationDate()) >= 0){
    	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
    	                        		record.setAccountStatusId("TF-C");
    	                            	tradeProduct.setAccountStatusId("CURRENT");
    	                        	} else if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
    	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
    	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) > 0) {
        	                    		record.setAccountStatusId("TF-A");
        	                        	tradeProduct.setAccountStatusId("EXPIRED");
    	                        	}	                    		
                    			} else if(DATE_COMPARATOR.compare(tradeProduct.getNegotiationDate(), tradeProduct.getLastReinstatementDate()) > 0){
    	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) == 0){
    	                        		record.setAccountStatusId("TF-C");
    	                            	tradeProduct.setAccountStatusId("CURRENT");
    	                        	} else  if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getNegotiationDate()) < 0
    	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
    	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) > 0) {
        	                    		record.setAccountStatusId("TF-A");
        	                        	tradeProduct.setAccountStatusId("EXPIRED");
    	                        	}	                    		
                    			}
                    		} else if(record.getLastReinstatementDate() != null){
                				if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
	                        		record.setAccountStatusId("TF-C");
	                            	tradeProduct.setAccountStatusId("CURRENT");
	                        	} else if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) > 0) {
    	                    		record.setAccountStatusId("TF-A");
    	                        	tradeProduct.setAccountStatusId("EXPIRED");
	                        	}
                    		} else if(record.getNegotiationDate() != null){
                				if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) == 0){
	                        		record.setAccountStatusId("TF-C");
	                            	tradeProduct.setAccountStatusId("CURRENT");
	                        	} else  if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getNegotiationDate()) < 0
	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) > 0) {
    	                    		record.setAccountStatusId("TF-A");
    	                        	tradeProduct.setAccountStatusId("EXPIRED");
	                        	}
                    		}
                    	}
                    } else if (tradeProduct.getDocumentClass().toString().equalsIgnoreCase("IC")) {
                    	if (tradeProduct.getAccountStatusId().equalsIgnoreCase("OPEN")) {
                    		record.setAccountStatusId("TF-C");
                    	} else {
                    		record.setAccountStatusId("TF-B");
                    	}
                    	tradeProduct.setDocumentClass(DocumentClass.LC);
                    }

                    if(!"TF-C".equalsIgnoreCase(record.getAccountStatusId())){
                        record.setOutstandingContingentAssets(BigDecimal.ZERO);
                        record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                    }

//            	Calendar cal = Calendar.getInstance();
//                Date tempDate = cal.getTime();
                    Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);

                    if(record.getMaturityDate() != null) {
                          System.out.println("tempdate:" + tempDate + " > maturitydate:" + record.getMaturityDate() + " = " + DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()));
                    }

                    // Below code is unnecessary
                /*
                if(record.getMaturityDate() != null && DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()) > 0) {
                */
                	/*if("CURRENT".equalsIgnoreCase(record.getAccountStatusId()) ||
                			"ACKNOWLEDGED".equalsIgnoreCase(record.getAccountStatusId())||
                			"ACCEPTED".equalsIgnoreCase(record.getAccountStatusId())||
                			"NEGOTIATED".equalsIgnoreCase(record.getAccountStatusId())){*/
                /*
                    if(!"TF311".equalsIgnoreCase(record.getProductId()) &&
             		   !"TF312".equalsIgnoreCase(record.getProductId()) &&
                	   !"TF313".equalsIgnoreCase(record.getProductId()) &&
                	   !"TF314".equalsIgnoreCase(record.getProductId()) &&
                	   !"TF316".equalsIgnoreCase(record.getProductId())){
                		record.setAccountStatusId("CANCELLED");
                		record.setClosedDate(record.getMaturityDate());
                		record.setOutstandingContingentAssets(BigDecimal.ZERO);
                		record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                	}
                }
                */
                }

                //Retrieve from a new table where settlement book code will be stored

                if(DocumentClass.LC.equals(tradeProduct.getDocumentClass()) && DocumentSubType1.REGULAR.equals(tradeProduct.getDocumentSubType1())){
                    if("29".equalsIgnoreCase(record.getSecurityCode()) ){
                        // record.setAppraisal(silverlakeLocalDao.getAppraisalDetails(tradeProduct.getMainCifNumber())); //COMMENT OUT FOR LOCAL
                    }
                    //Appraisal appraisal = silverlakeLocalDao.getAppraisalDetails(tradeProduct.getMainCifNumber()); //COMMENT OUT FOR LOCAL
                }

//            System.out.println(""+tradeProduct.getDocumentClass() +" = "+tradeProduct.getDocumentSubType1() + " = " + tradeProduct.getDocumentSubType2());
                if("USANCE".equalsIgnoreCase(tradeProduct.getDocumentSubType2())){
                       System.out.println("USANCE");
                }

                System.out.println("DOCUMENT NUMBER:"+tradeProduct.getApplicationAccountId()+"|SettlementBookCode:"+tradeProductDao.getSettlementBookCodeByDocumentNumber(tradeProduct.getApplicationAccountId()));
                if(tradeProductDao.getSettlementBookCodeByDocumentNumber(tradeProduct.getApplicationAccountId()) != null){
                    record.setSettlementBlockCode(tradeProductDao.getSettlementBookCodeByDocumentNumber(tradeProduct.getApplicationAccountId()));
                }
                record.setModeOfPayment(tradeProduct.getDocumentClass().getModeOfPayment());
                
                if("TF119".equalsIgnoreCase(record.getProductId()))
                {
                	record.setContingentAssetsGlNumber("-");
                	record.setContingentLiabilitiesGlNumber("-");
                	record.setOutstandingContingentAssets(BigDecimal.ZERO);
                	record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                }
                
                //TODO UA MATURITY

                record.setCreditFacilityCode(generateCreditFacilityCode(tradeProduct.getMaturityDate(), tradeProduct.getOpenDate()));
                masterFileRecords.add(record);
                System.out.println(record);
            }catch(Exception e){
                System.err.println("DOCUMENT NUMBER " + tradeProduct.getApplicationAccountId() + " WAS REMOVED DUE TO AN ERROR");
                e.printStackTrace();
                continue;
            }
        }

        return masterFileRecords;
    }

    public void setTradeProductDao(TradeProductDao tradeProductDao) {
        this.tradeProductDao = tradeProductDao;
    }

    public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao) {
        this.silverlakeLocalDao = silverlakeLocalDao;
    }
    
    private String generateCreditFacilityCode(Date maturityDate, Date issueDate){
        if(maturityDate == null){
            return LONG_TERM;
        }
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(issueDate);
    	calendar.add(Calendar.YEAR,1);
    	
    	if(maturityDate.after(calendar.getTime())){
    		return LONG_TERM;
    	}
    	
    	return SHORT_TERM;
    }

    private List<TradeProduct> getAllActiveProducts(String appDate){

        System.out.println("getAllActiveProducts:"+ appDate);

//        List<TradeProduct> activeProducts = tradeProductDao.getActiveLettersOfCredit();
        //List<TradeProduct> activeProducts = tradeProductDao.getActiveOpenAccounts(appDate);

        // LC
        List<TradeProduct> activeProducts = tradeProductDao.getActiveLettersOfCredit(appDate);

        // for adding a new record for LC Regular that is adjust to LC Cash
        List<TradeProduct> modifiedLcProduct = new ArrayList<TradeProduct>();
        for(TradeProduct tradeProduct:activeProducts)
        {        	
        	if(tradeProduct.getDocumentSubType1().equals(DocumentSubType1.REGULAR) && tradeProduct.getCashFlag() == 1 && !"USANCE".equalsIgnoreCase(tradeProduct.getDocumentSubType2())){            	
            	BigDecimal originalAmount = tradeProduct.getOriginalAmount();
            	BigDecimal negotiatedAmount = tradeProduct.getTotalNegotiatedAmount();
            	BigDecimal cashAmount = tradeProduct.getCashAmount();
            	cashAmount = cashAmount.subtract(negotiatedAmount);
            	BigDecimal outstandingAmount = originalAmount.subtract(cashAmount);
//				BigDecimal outstandingAmount = originalAmount.subtract(cashAmount);
//            	if(negotiatedAmount.compareTo(BigDecimal.ZERO) > 0){
//            		BigDecimal remainingNegoAmount = cashAmount.subtract(negotiatedAmount);
//            		if(remainingNegoAmount.compareTo(BigDecimal.ZERO) < 0){
//            			remainingNegoAmount = remainingNegoAmount.negate();
//            			cashAmount = BigDecimal.ZERO;
//            		}else{
//            			cashAmount = remainingNegoAmount;
//            		}
//            		outstandingAmount =outstandingAmount.subtract(remainingNegoAmount);
//            	}

            	if(outstandingAmount.compareTo(BigDecimal.ZERO) == -1){
            		outstandingAmount = BigDecimal.ZERO;
            	}

            	if(cashAmount.compareTo(BigDecimal.ZERO) == -1 || (outstandingAmount.compareTo(BigDecimal.ZERO) <= 0 && 
            			originalAmount.compareTo(BigDecimal.ZERO) <= 0 && "CLOSED".equalsIgnoreCase(tradeProduct.getAccountStatusId()))){
            		cashAmount = BigDecimal.ZERO;
            	}

            	tradeProduct.setOutstandingContingentLiabilities(outstandingAmount);
            	tradeProduct.setOutstandingContingentAssets(outstandingAmount);
            	tradeProduct.setContingentAssetsGlNumber(tradeProduct.getPreviousAssetsGlNumber());
            	tradeProduct.setContingentLiabilitiesGlNumber(tradeProduct.getPreviousLiabilitiesGlNumber());
            	tradeProduct.setProductId(tradeProduct.getPreviousProductID());
            	tradeProduct.setCashAmount(cashAmount);
            	

            	if("REINSTATED".equalsIgnoreCase(tradeProduct.getAccountStatusId())){
            		try{
	            		tradeProduct.setAccountStatusId("CURRENT");
	            		Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
	            		//System.out.println("XXXXXXXXXXXXXXXX:::" + tradeProduct.getApplicationAccountId() + "tradeProduct.getMaturityDate():::" + tradeProduct.getMaturityDate() + "tradeProduct.getLastReinstatementDate():::" + tradeProduct.getLastReinstatementDate());
	            		if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
	            				&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) > 0
	            				&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0){
	            			//System.out.println("XXXXXXXXXXXXXXXX:::" + tradeProduct.getApplicationAccountId());
	            			tradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
	            			tradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
	            			tradeProduct.setAccountStatusId("EXPIRED");
	            		}
            		}catch(Exception e){
            			System.out.println();
            			e.printStackTrace();
            		}
            	} else if(("EXPIRED".equalsIgnoreCase(tradeProduct.getAccountStatusId())) && tradeProduct.getNegotiationDate() != null) {
					try {						
						Date appDate2 = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
						String negoDateString = dateFormat.format(tradeProduct.getNegotiationDate());
						String masterDateString =dateFormat.format(appDate2);
				        Date negoDate = dateFormat.parse(negoDateString);
				        Date masterDate = dateFormat.parse(masterDateString);
				        
				        System.out.println("masterDate" + masterDate);
				        System.out.println("negoDate" + negoDate);
				        
						if(masterDate.equals(negoDate)){
							tradeProduct.setAccountStatusId("CURRENT");
						}
					} catch (ParseException e) {
						System.out.println();
						e.printStackTrace();
					}
            	} else if(("OPEN".equalsIgnoreCase(tradeProduct.getAccountStatusId()) || "CANCELLED".equalsIgnoreCase(tradeProduct.getAccountStatusId()))
            			&& tradeProduct.getClosedDate() == null && tradeProduct.getCancelledDate() == null){           		
					try {
						Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
						if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0){							
							Calendar tempCancelDate = GregorianCalendar.getInstance();
							tempCancelDate.setTime(tradeProduct.getMaturityDate());
							tempCancelDate.add(GregorianCalendar.DATE, +1);
							Date cancelDate = tempCancelDate.getTime();
							
							tradeProduct.setAccountStatusId("CANCELLED");
							tradeProduct.setCancelledDate(cancelDate);
						}
					} catch (ParseException e) {
						System.out.println();
						e.printStackTrace();
					}        		            		
            	}
            	            	
            	//for creating new record with document number for Adjusted Regular to Cash
                TradeProduct tempTradeProduct = tradeProduct.getCloneTradeProduct();            	       	
                StringBuilder applicationAccountId = new StringBuilder(tempTradeProduct.getApplicationAccountId());               
                if(applicationAccountId.toString().startsWith("FX")){
                	applicationAccountId.replace(4,6,"98");
                    tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                    tempTradeProduct.setContingentAssetsGlNumber("-");
                    tempTradeProduct.setContingentLiabilitiesGlNumber("246210101000");
                    tempTradeProduct.setProductId("TF113");
                }else if(applicationAccountId.toString().startsWith("DM")){
                	applicationAccountId.replace(4,6,"99");
                	tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                    tempTradeProduct.setContingentAssetsGlNumber("-");
                    tempTradeProduct.setContingentLiabilitiesGlNumber("246210102000");
                    tempTradeProduct.setProductId("TF217");
                }else{
                    if(tempTradeProduct.getDocumentType().equals(DocumentType.FOREIGN.toString())) {
                    	applicationAccountId.replace(4, 6, "98");
                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                        tempTradeProduct.setContingentAssetsGlNumber("-");
                        tempTradeProduct.setContingentLiabilitiesGlNumber("246210101000");
                        tempTradeProduct.setProductId("TF113");
                    }else{
                    	applicationAccountId.replace(4, 6, "99");
                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                        tempTradeProduct.setContingentAssetsGlNumber("-");
                        tempTradeProduct.setContingentLiabilitiesGlNumber("246210102000");
                        tempTradeProduct.setProductId("TF217");
                    }
                }
            	tempTradeProduct.setOutstandingContingentLiabilities(cashAmount);
            	tempTradeProduct.setOutstandingContingentAssets(cashAmount);
            	tempTradeProduct.setAccountStatusId("CURRENT");
                modifiedLcProduct.add(tempTradeProduct);// add modified trade product to modifiedLcProduct
                
                if(outstandingAmount.compareTo(BigDecimal.ZERO) == 0 && !"CLOSED".equalsIgnoreCase(tradeProduct.getAccountStatusId())){
            		List<Integer> revIdList = tradeProductDao.getAuditRevId(tradeProduct.getApplicationAccountId(), appDate);
            		Calendar tempClosedDate = GregorianCalendar.getInstance();
            		Boolean isAdjustedToFullCash = false;
            		Boolean isRegularAmountZero = true;
            		BigDecimal lastOutstandingAmountAudit = BigDecimal.ONE; 
            		BigDecimal lastCashAmount = BigDecimal.ONE;
            		int numberOfRevId = revIdList.size();
            		for(Integer revId:revIdList){
            			System.out.println("revId: " + revId + " DocNum: " + tradeProduct.getApplicationAccountId());
            			List<TradeProduct> letterOfCreditAuditList = tradeProductDao.getLetterOfCreditAuditByRevId(revId);
            			
            			Iterator<TradeProduct> letterOfCreditAuditDetails = letterOfCreditAuditList.iterator();
            			while(letterOfCreditAuditDetails.hasNext() && isAdjustedToFullCash == false && isRegularAmountZero == true){
            				TradeProduct letterOfCreditAudit = letterOfCreditAuditDetails.next();
            				BigDecimal auditOriginalAmount = letterOfCreditAudit.getOriginalAmount();
                        	BigDecimal auditNegotiatedAmount = letterOfCreditAudit.getTotalNegotiatedAmount();
                        	BigDecimal auditCashAmount = letterOfCreditAudit.getCashAmount();
                        	auditCashAmount = auditCashAmount.subtract(auditNegotiatedAmount);
                        	BigDecimal auditOutstandingAmount = auditOriginalAmount.subtract(auditCashAmount);
                        	
                        	System.out.println("auditOriginalAmount: " + auditOriginalAmount);
                        	//System.out.println("auditNegotiatedAmount: " + auditNegotiatedAmount);
                        	//System.out.println("auditCashAmount: " + auditCashAmount);
                        	System.out.println("auditOutstandingAmount: " + auditOutstandingAmount);
                        	System.out.println("lastOutstandingAmountAudit: " + lastOutstandingAmountAudit);
                        	System.out.println("lastCashAmount: " + lastCashAmount);
                        	System.out.println("letterOfCreditAudit.getCashAmount(): " + letterOfCreditAudit.getCashAmount());

                        	if(auditOutstandingAmount.compareTo(BigDecimal.ZERO) <= 0 && letterOfCreditAudit.getCashAmount().compareTo(BigDecimal.ZERO) == 1
                        			&& numberOfRevId == 1){           		
                        		isAdjustedToFullCash = true;
                        		tempClosedDate.setTime(letterOfCreditAudit.getClosedDate());
                        	} else if((auditOutstandingAmount.compareTo(BigDecimal.ZERO) == 1 && letterOfCreditAudit.getCashAmount().compareTo(BigDecimal.ZERO) >= 0)
                        			&& (lastOutstandingAmountAudit.compareTo(BigDecimal.ZERO) <= 0 && lastCashAmount.compareTo(BigDecimal.ZERO) == 1)){           		
                        		isAdjustedToFullCash = true;
                        	} else {
                        		lastOutstandingAmountAudit = auditOutstandingAmount;
                        		lastCashAmount = letterOfCreditAudit.getCashAmount();
                        		tempClosedDate.setTime(letterOfCreditAudit.getClosedDate());
                        	}
                        	
                        	if(auditOutstandingAmount.compareTo(BigDecimal.ZERO) == 1 && letterOfCreditAudit.getCashAmount().compareTo(BigDecimal.ZERO) >= 0){
                        		isRegularAmountZero = false;
                        	}
            			}
            		}
            		
            		if(isAdjustedToFullCash) {
						Date closedDate = tempClosedDate.getTime();
						
						tradeProduct.setAccountStatusId("CLOSED");
						tradeProduct.setClosedDate(closedDate);
            		}
            	}
            }else if(tradeProduct.getDocumentSubType1().equals(DocumentSubType1.REGULAR) && 
        			(tradeProductDao.checkIfTenorChange(tradeProduct.getApplicationAccountId(), appDate) != null && 
        					tradeProductDao.checkIfTenorChange(tradeProduct.getApplicationAccountId(), appDate).equalsIgnoreCase("Y"))){        	       		
        		//for creating new record with document number for Change of Tenor from Regular Sight to Usance
            	TradeProduct tempTradeProduct = tradeProduct.getCloneTradeProduct();
            	StringBuilder applicationAccountId = new StringBuilder(tempTradeProduct.getApplicationAccountId());               
                if(applicationAccountId.toString().startsWith("FX")){
                	applicationAccountId.replace(4,6,"88");
                    tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                    tempTradeProduct.setContingentAssetsGlNumber(tradeProduct.getContingentAssetsGlNumber());
                    tempTradeProduct.setContingentLiabilitiesGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
                    tempTradeProduct.setProductId(tradeProduct.getProductId());
                }else if(applicationAccountId.toString().startsWith("DM")){
                	applicationAccountId.replace(4,6,"89");
                	tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                	tempTradeProduct.setContingentAssetsGlNumber(tradeProduct.getContingentAssetsGlNumber());
                    tempTradeProduct.setContingentLiabilitiesGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
                    tempTradeProduct.setProductId(tradeProduct.getProductId());
                }else{
                    if(tempTradeProduct.getDocumentType().equals(DocumentType.FOREIGN.toString())) {
                    	applicationAccountId.replace(4, 6, "88");
                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                        tempTradeProduct.setContingentAssetsGlNumber(tradeProduct.getContingentAssetsGlNumber());
                        tempTradeProduct.setContingentLiabilitiesGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
                        tempTradeProduct.setProductId(tradeProduct.getProductId());
                    }else{
                    	applicationAccountId.replace(4, 6, "89");
                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                        tempTradeProduct.setContingentAssetsGlNumber(tradeProduct.getContingentAssetsGlNumber());
                        tempTradeProduct.setContingentLiabilitiesGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
                        tempTradeProduct.setProductId(tradeProduct.getProductId());
                    }
                }
            	tempTradeProduct.setOutstandingContingentLiabilities(tradeProduct.getOriginalAmount());
            	tempTradeProduct.setOutstandingContingentAssets(tradeProduct.getOriginalAmount());
            	tempTradeProduct.setAccountStatusId(tradeProduct.getAccountStatusId());
                modifiedLcProduct.add(tempTradeProduct);// add modified trade product to modifiedLcProduct
                
                tradeProduct.setContingentAssetsGlNumber(tradeProduct.getPreviousAssetsGlNumber());
                tradeProduct.setContingentLiabilitiesGlNumber(tradeProduct.getPreviousLiabilitiesGlNumber());
                tradeProduct.setProductId(tradeProduct.getPreviousProductID());
                tradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
                tradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                tradeProduct.setClosedDate(tradeProductDao.getRegularSightCloseDate(tradeProduct.getApplicationAccountId(), appDate));
                tradeProduct.setAccountStatusId("CLOSED");
            	
        	} else if(tradeProduct.getDocumentSubType1().equals(DocumentSubType1.STANDBY)){
        		List<Integer> revIdList = tradeProductDao.getAuditRevId(tradeProduct.getApplicationAccountId(), appDate);
        		Boolean isStandbyTaggingAdjusted = false;
        		String lastStandbyTagging = null;
        		Calendar tempClosedDate = GregorianCalendar.getInstance();
        		for(Integer revId:revIdList){
        			System.out.println("----STANDBY---- DocNum: " + tradeProduct.getApplicationAccountId());
        			List<TradeProduct> letterOfCreditAuditList = tradeProductDao.getLetterOfCreditAuditByRevId(revId);
        			
        			Iterator<TradeProduct> letterOfCreditAuditDetails = letterOfCreditAuditList.iterator();
        			while(letterOfCreditAuditDetails.hasNext() && isStandbyTaggingAdjusted == false){
        				TradeProduct letterOfCreditAudit = letterOfCreditAuditDetails.next();
        				String standbyTagging = letterOfCreditAudit.getStandbyTagging();
        				
        				if(lastStandbyTagging != null && !lastStandbyTagging.equalsIgnoreCase(standbyTagging)){
        					isStandbyTaggingAdjusted = true;
        				} else {
        					lastStandbyTagging = standbyTagging;
        					tempClosedDate.setTime(letterOfCreditAudit.getClosedDate());
        				}
        				System.out.println("isStandbyTaggingAdjusted: " + isStandbyTaggingAdjusted);
        				
        				if(isStandbyTaggingAdjusted){
        					//for creating new record with document number for Adjustment of Standby Tagging from Performance to Financial or Financial to Performance
        					TradeProduct tempTradeProduct = tradeProduct.getCloneTradeProduct();
        	            	StringBuilder applicationAccountId = new StringBuilder(tempTradeProduct.getApplicationAccountId());
        	            	String openingStandbyTagging = tradeProductDao.getOpeningStandbyTagging(tradeProduct.getApplicationAccountId());
        	            	Map<String, String> refProductInfo = new HashMap<String, String>();
        	            	if(openingStandbyTagging.equalsIgnoreCase("PERFORMANCE")){       		
        	            		if(applicationAccountId.toString().startsWith("FX") || tempTradeProduct.getDocumentType().equals(DocumentType.FOREIGN.toString())){
        	            			refProductInfo = tradeProductDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "FINANCIAL");
            	                	applicationAccountId.replace(4,6,"78");
            	                    tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
            	                    tempTradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	                    tempTradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	                    tempTradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else if(applicationAccountId.toString().startsWith("DM") || tempTradeProduct.getDocumentType().equals(DocumentType.DOMESTIC.toString())){
            	                	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
            	                	applicationAccountId.replace(4,6,"79");
            	                	tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
            	                    tempTradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	                    tempTradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	                    tempTradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else{
        	                    	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
        	                    	applicationAccountId.replace(4, 6, "79");
        	                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
            	                    tempTradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	                    tempTradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	                    tempTradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
            	                }
        	            		
        	            		if(lastStandbyTagging.equalsIgnoreCase(openingStandbyTagging)){
                	            	tempTradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                	            	tempTradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
                	            	tempTradeProduct.setClosedDate(tempClosedDate.getTime());
                	            	tempTradeProduct.setAccountStatusId("CLOSED");
        	            		} else {
        	            			tempTradeProduct.setOutstandingContingentLiabilities(tradeProduct.getOriginalAmount());
                	            	tempTradeProduct.setOutstandingContingentAssets(tradeProduct.getOriginalAmount());
                	            	tempTradeProduct.setAccountStatusId(tradeProduct.getAccountStatusId());

            	            		if(applicationAccountId.toString().startsWith("FX") || tempTradeProduct.getDocumentType().equals(DocumentType.FOREIGN.toString())){
            	            			refProductInfo = tradeProductDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "PERFORMANCE");
            	            			tradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	            			tradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	            			tradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else if(applicationAccountId.toString().startsWith("DM") || tempTradeProduct.getDocumentType().equals(DocumentType.DOMESTIC.toString())){
                	                	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
                	                	tradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
                	                    tradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
                	                    tradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else{
            	                    	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
            	                    	tradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
                	                    tradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
                	                    tradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
                	                }
                	                tradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
                	                tradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                	                tradeProduct.setClosedDate(tempClosedDate.getTime());
                	                tradeProduct.setAccountStatusId("CLOSED");
        	            		}
        	            	} else if(openingStandbyTagging.equalsIgnoreCase("FINANCIAL")){
        	            		if(applicationAccountId.toString().startsWith("FX") || tempTradeProduct.getDocumentType().equals(DocumentType.FOREIGN.toString())){
        	                    	refProductInfo = tradeProductDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "PERFORMANCE");
            	                	applicationAccountId.replace(4,6,"78");
            	                    tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
            	                    tempTradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	                    tempTradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	                    tempTradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else if(applicationAccountId.toString().startsWith("DM") || tempTradeProduct.getDocumentType().equals(DocumentType.DOMESTIC.toString())){
        	                    	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
            	                	applicationAccountId.replace(4,6,"79");
            	                	tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
            	                    tempTradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	                    tempTradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	                    tempTradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
            	                }else{
        	                    	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "PERFORMANCE");
        	                    	applicationAccountId.replace(4, 6, "79");
        	                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
            	                    tempTradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	                    tempTradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	                    tempTradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
            	                }
        	            		
        	            		if(lastStandbyTagging.equalsIgnoreCase(openingStandbyTagging)){
                	            	tempTradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                	            	tempTradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
                	            	tempTradeProduct.setClosedDate(tempClosedDate.getTime());
                	            	tempTradeProduct.setAccountStatusId("CLOSED");
        	            		} else {
        	            			tempTradeProduct.setOutstandingContingentLiabilities(tradeProduct.getOriginalAmount());
                	            	tempTradeProduct.setOutstandingContingentAssets(tradeProduct.getOriginalAmount());
                	            	tempTradeProduct.setAccountStatusId(tradeProduct.getAccountStatusId());
                	            	
            	            		if(applicationAccountId.toString().startsWith("FX") || tempTradeProduct.getDocumentType().equals(DocumentType.FOREIGN.toString())){
            	            			refProductInfo = tradeProductDao.getRefProductInfo("LC", "FOREIGN", "STANDBY", "SIGHT", "FINANCIAL");
            	            			tradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	            			tradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	            			tradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else if(applicationAccountId.toString().startsWith("DM") || tempTradeProduct.getDocumentType().equals(DocumentType.DOMESTIC.toString())){
                	                	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
                	                	tradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
                	                	tradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
                	                	tradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
                	                }else{
            	                    	refProductInfo = tradeProductDao.getRefProductInfo("LC", "DOMESTIC", "STANDBY", "SIGHT", "FINANCIAL");
            	                    	tradeProduct.setContingentAssetsGlNumber(refProductInfo.get("DEBIT_CODE"));
            	                    	tradeProduct.setContingentLiabilitiesGlNumber(refProductInfo.get("CREDIT_CODE"));
            	                    	tradeProduct.setProductId(refProductInfo.get("PRODUCTID"));
                	                }            	            		
                	                tradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
                	                tradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                	                tradeProduct.setClosedDate(tempClosedDate.getTime());
                	                tradeProduct.setAccountStatusId("CLOSED");
        	            		}
        	            	}       	            	
        	            	modifiedLcProduct.add(tempTradeProduct);// add modified trade product to modifiedLcProduct
         				}
        			}
        		}
        	}
        	
        	TradeProduct icTradeProduct = tradeProduct.getCloneTradeProduct();

        	System.out.println("LC number: " + tradeProduct.getApplicationAccountId());
        	List<Map<String, Object>> icAccounts = tradeProductDao.getIcAccount(tradeProduct.getApplicationAccountId(), appDate);
        	
        	for(Map<String, Object> icAccount : icAccounts) {
        		if (icAccount!=null) {
        			icTradeProduct = tradeProduct.getCloneTradeProduct();
        			String icNumber = icAccount.get("ICNUMBER").toString();
                    String documentType = icTradeProduct.getDocumentType().toString().toUpperCase();
                    Date modifiedDate = (Date) icAccount.get("LASTMODIFIEDDATE");
                	icNumber = icNumber.substring(0, icNumber.length()-1).concat("-").concat(icNumber.substring(icNumber.length()-1, icNumber.length()));
                	
                	System.out.println("LC Account number: " + icAccount.get("DOCUMENTNUMBER"));
                	System.out.println("IC Account number: " + icNumber);
                	System.out.println("IC Amount: " + (BigDecimal) icAccount.get("NEGOTIATIONAMOUNT"));            	
                	
                	icTradeProduct.setApplicationAccountId(icNumber);
                	icTradeProduct.setOutstandingContingentAssets((BigDecimal) icAccount.get("NEGOTIATIONAMOUNT"));
                	icTradeProduct.setOutstandingContingentLiabilities((BigDecimal) icAccount.get("NEGOTIATIONAMOUNT"));
                    
                    if (icAccount.get("LCNEGOTIATIONDISCREPANCYSTATUS").toString().equalsIgnoreCase("CLOSED")) {
                    	icTradeProduct.setAccountStatusId("CLOSED");
                    	icTradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
                    	icTradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                    	icTradeProduct.setClosedDate(modifiedDate);
                    	icTradeProduct.setNegotiationDate(modifiedDate);
                    	icTradeProduct.removeDates();   
                    } else {
                    	icTradeProduct.setAccountStatusId("OPEN");
                    	icTradeProduct.setOpenDate(modifiedDate);
                    	icTradeProduct.removeCloseDate();
                    	icTradeProduct.removeDates();                    	
                    }                    
                    
                    Map<String, String> icRefProductInfo = tradeProductDao.getRefProductInfo("LC", documentType, "NEGOTIATION", "DISCREPANCY", "NULL");
                    icTradeProduct.setProductId(icRefProductInfo.get("PRODUCTID"));
                    icTradeProduct.setContingentAssetsGlNumber(icRefProductInfo.get("DEBIT_CODE"));
                    icTradeProduct.setContingentLiabilitiesGlNumber(icRefProductInfo.get("CREDIT_CODE"));
                    icTradeProduct.setDocumentClass("IC");
                	
                    modifiedLcProduct.add(icTradeProduct);
            	}
        	}        	
        }       
        //add the trade product with new document number
        activeProducts.addAll(modifiedLcProduct);

        // DA
        activeProducts.addAll(tradeProductDao.getActiveDocumentsAgainstAcceptance(appDate));
        System.out.println(activeProducts.size());

        // DP
        activeProducts.addAll(tradeProductDao.getActiveDocumentsAgainstPayment(appDate));
        System.out.println(activeProducts.size());

        // OA
        activeProducts.addAll(tradeProductDao.getActiveOpenAccounts(appDate));
        System.out.println(activeProducts.size());

        // DR
        activeProducts.addAll(tradeProductDao.getActiveDirectRemittances(appDate));
        System.out.println(activeProducts.size());

        // EBC,DBC
        activeProducts.addAll(tradeProductDao.getActiveBillsCollection(appDate));
        System.out.println(activeProducts.size());
        
        //EBP/DBP
        activeProducts.addAll(tradeProductDao.getActiveBillsPurchase(appDate));
        System.out.println(activeProducts.size());

        //Advance Payment
        activeProducts.addAll(tradeProductDao.getActiveAdvancePayment(appDate));
        System.out.println(activeProducts.size());

        // LC Advising
        activeProducts.addAll(tradeProductDao.getActiveExportAdvising(appDate));
        System.out.println(activeProducts.size());

        // BG
        activeProducts.addAll(tradeProductDao.getActiveBankGuarantee(appDate));
        System.out.println(activeProducts.size());

        System.out.println("with active product size:" + activeProducts.size());

        //TODO:: EXPORTS and CDT

        return activeProducts;
    }

    private Map<String,String> getContingentByTradeServiceIdAndType(String tradeServiceId, String type){
//        System.out.println("getContingentByTradeServiceIdAndType");
//        System.out.println("tradeServiceId:"+tradeServiceId);
//        System.out.println("type:"+type);
        List<Map<String,String>> contingents = tradeProductDao.getContingentByTradeServiceIdAndType(tradeServiceId,type);
        if(contingents.isEmpty()){
            return new HashMap<String,String>();
        }
        return contingents.get(0);
    }


    @Override
    public List<MasterFileRecord> getMasterFilesException(String appDate) {

        System.out.println("getMasterFilesException");

        List<TradeProduct> activeProducts = getAllActiveProductsException(appDate);
        
        //set outstanding balance to 0 of EBC for EBP with prior EBC
        for(TradeProduct tempTradeProduct:activeProducts){
            if("TF513".equalsIgnoreCase(tempTradeProduct.getProductId()) && tempTradeProduct.getNegoNumber()!= null){
            	for(TradeProduct temp00:activeProducts){
            		if(tempTradeProduct.getNegoNumber().equalsIgnoreCase(temp00.getApplicationAccountId())){
            			temp00.setOutstandingContingentAssets(BigDecimal.ZERO);
            			temp00.setOutstandingContingentLiabilities(BigDecimal.ZERO);
            			break;
            		}
            	}        	         	
            }
        }
        
        List<MasterFileRecord> masterFileRecords = new ArrayList<MasterFileRecord>();
        for(TradeProduct tradeProduct : activeProducts){

            try{

                MasterFileRecord record = new MasterFileRecord(tradeProduct);

                record.setProductId(tradeProduct.getProductId());

                // record.setIndustryCode(silverlakeLocalDao.getIndustryCode(record.getCustomerId())); //COMMENT OUT FOR LOCAL

//            Map<String,String> creationContingentAsset = getContingentByTradeServiceIdAndType(tradeProduct.getCreationTradeServiceId(), ASSET);
//            Map<String,String> creationContingentLiability = getContingentByTradeServiceIdAndType(tradeProduct.getCreationTradeServiceId(), LIABILITY);

                record.setOutstandingBookCode("RG");
//            if(!creationContingentAsset.isEmpty()){
//                record.setContingentAssetsGlNumber(creationContingentAsset.get(ACCOUNTINGCODE));
//            }
//            if(!creationContingentLiability.isEmpty()){
//                record.setContingentLiabilitiesGlNumber(creationContingentLiability.get(ACCOUNTINGCODE));
//            }

                if ("TF217".equalsIgnoreCase(record.getProductId())||"TF113".equalsIgnoreCase(record.getProductId())) {
                	//set ContingentLiabilitiesGlNumber to ContingentAssetsGlNumber
                    System.out.println("CASH LC ContingentLiabilitiesGlNumber to ContingentAssetsGlNumber");
                    record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
                    record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                }
                
                System.out.println("tradeProduct.getGlAccountType():"+tradeProduct.getGlAccountType());

                if("L".equals(tradeProduct.getGlAccountType())){
                    //Handles Amounts
                    //reverse the signs
                    if(!"-".equals(tradeProduct.getContingentAssetsGlNumber()) && tradeProduct.getOutstandingContingentAssets()!=null){
                       System.out.println("ContingentAssets");
                       System.out.println("tradeProduct.getOutstandingContingentAssets():"+tradeProduct.getOutstandingContingentAssets());
                       System.out.println("tradeProduct.getOutstandingContingentAssets() NEGATE:"+tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).negate());
                        record.setOutstandingContingentAssets(tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).negate());
                    }else{
                    	record.setOutstandingContingentAssets(BigDecimal.ZERO);
                    }
                    if(!"-".equals(tradeProduct.getContingentLiabilitiesGlNumber()) && tradeProduct.getOutstandingContingentLiabilities()!=null){
                     	System.out.println("ContingentLiabilities");
                     	System.out.println("tradeProduct.getOutstandingContingentLiabilities():"+tradeProduct.getOutstandingContingentLiabilities());
                     	System.out.println("tradeProduct.getOutstandingContingentLiabilities() NEGATE:"+tradeProduct.getOutstandingContingentLiabilities().setScale(2, BigDecimal.ROUND_FLOOR).negate());
                        record.setOutstandingContingentLiabilities(tradeProduct.getOutstandingContingentLiabilities().setScale(2, BigDecimal.ROUND_FLOOR));
                    }else{
                    	record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                    }
                }

                if("A".equals(tradeProduct.getGlAccountType())){
                     System.out.println("Swapping A");
                    //Handles GL Account Number
                    String tempStringCL =tradeProduct.getContingentLiabilitiesGlNumber();
                    String tempStringCA =tradeProduct.getContingentAssetsGlNumber();
                    record.setContingentAssetsGlNumber(tempStringCL);
                    record.setContingentLiabilitiesGlNumber(tempStringCA);

//                if(tradeProduct.getContingentLiabilitiesGlNumber()!=null){
//                    record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
//                }
//                if(tradeProduct.getContingentAssetsGlNumber()!=null){
//                    record.setContingentLiabilitiesGlNumber(tradeProduct.getContingentAssetsGlNumber());
//                }
                }

                // LC Cash
                if ("TF217".equalsIgnoreCase(record.getProductId())||"TF113".equalsIgnoreCase(record.getProductId())) {
                     System.out.println("CASH LC HERE");
                    //Handles Amounts
                    //reverse the signs
//                     record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
//                     record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
//                    if(!"-".equals(tradeProduct.getContingentAssetsGlNumber()) && tradeProduct.getOutstandingContingentAssets()!=null){
//                          System.out.println("ContingentAssets");
//                         System.out.println("tradeProduct.getOutstandingContingentAssets():"+tradeProduct.getOutstandingContingentAssets());
//                          System.out.println("tradeProduct.getOutstandingContingentAssets() NEGATE:"+tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).negate());
//                        record.setOutstandingContingentAssets(tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR));
//                    }
//                    else{
//                    	record.setOutstandingContingentAssets(BigDecimal.ZERO);
//                    }
                    
//                    if(!"-".equals(tradeProduct.getContingentLiabilitiesGlNumber()) && tradeProduct.getOutstandingContingentLiabilities()!=null){
//                         System.out.println("ContingentLiabilities");
//                         System.out.println("tradeProduct.getOutstandingContingentLiabilities():"+tradeProduct.getOutstandingContingentLiabilities());
//                        record.setOutstandingContingentLiabilities(tradeProduct.getOutstandingContingentLiabilities().setScale(2, BigDecimal.ROUND_FLOOR));
//                        System.out.println("tradeProduct.getOutstandingContingentLiabilities():"+tradeProduct.getOutstandingContingentLiabilities());
//                    }else{
//                    	record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
//                    }
                    
                    //Handles GL Account Number Reversal
                                       System.out.println("tradeProduct.getContingentLiabilitiesGlNumber()"+tradeProduct.getContingentLiabilitiesGlNumber());
                     System.out.println("tradeProduct.getContingentAssetsGlNumber()"+tradeProduct.getContingentAssetsGlNumber());
/*                    String tempStringCL =tradeProduct.getContingentLiabilitiesGlNumber();
                    String tempStringCA =tradeProduct.getContingentAssetsGlNumber();
                    record.setContingentAssetsGlNumber(tempStringCL);
                    record.setContingentLiabilitiesGlNumber(tempStringCA);
*/
//                if(tradeProduct.getContingentLiabilitiesGlNumber()!=null){
//                	record.setContingentAssetsGlNumber(tradeProduct.getContingentLiabilitiesGlNumber());
//                }
//                if(tradeProduct.getContingentAssetsGlNumber()!=null){
//                	record.setContingentLiabilitiesGlNumber(tradeProduct.getContingentAssetsGlNumber());
//                }

//                Calendar cal = Calendar.getInstance();
//                Date tempDate = cal.getTime();
                     Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
                     if(tradeProduct.getOutstandingContingentAssets().setScale(2, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.ZERO) == 0 &&
                             !"TF-A".equalsIgnoreCase(record.getAccountStatusId())){
                     	record.setAccountStatusId("TF-B");
                     	tradeProduct.setAccountStatusId("CLOSED");
                     } else if(record.getMaturityDate() != null &&
                             DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()) >= 0 &&
                             !"TF-A".equalsIgnoreCase(record.getAccountStatusId()) && 
                             !"TF-B".equalsIgnoreCase(record.getAccountStatusId())){ 
                     	if(DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()) == 0){
                     		record.setAccountStatusId("TF-C");
                         	tradeProduct.setAccountStatusId("CURRENT");
                     	} else if(record.getLastReinstatementDate() != null && record.getNegotiationDate() != null){
                 			if(DATE_COMPARATOR.compare(tradeProduct.getLastReinstatementDate(), tradeProduct.getNegotiationDate()) >= 0){
 	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
 	                        		record.setAccountStatusId("TF-C");
 	                            	tradeProduct.setAccountStatusId("CURRENT");
 	                        	} else {
 	                        		record.setAccountStatusId("TF-D");
 	                            	tradeProduct.setAccountStatusId("MATURED");
 	                        	}	                    		
                 			} else if(DATE_COMPARATOR.compare(tradeProduct.getNegotiationDate(), tradeProduct.getLastReinstatementDate()) > 0){
 	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) == 0){
 	                        		record.setAccountStatusId("TF-C");
 	                            	tradeProduct.setAccountStatusId("CURRENT");
 	                        	} else {
 	                        		record.setAccountStatusId("TF-D");
 	                            	tradeProduct.setAccountStatusId("MATURED");
 	                        	}
                 			}
                 		} else if(record.getLastReinstatementDate() != null){
                     		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
                         		record.setAccountStatusId("TF-C");
                             	tradeProduct.setAccountStatusId("CURRENT");
                         	} else {
                         		record.setAccountStatusId("TF-D");
                             	tradeProduct.setAccountStatusId("MATURED");
                         	}
                     	} else {
                     		record.setAccountStatusId("TF-D");
                         	tradeProduct.setAccountStatusId("MATURED");
                     	}
                     }


                 } else {

                     // Any product that is NOT LC Cash
                 	
                 	if(DocumentClass.LC.equals(tradeProduct.getDocumentClass())){
                 		if(("EXPIRED".equalsIgnoreCase(tradeProduct.getAccountStatusId())) && tradeProduct.getNegotiationDate() != null) {
         					try {						
         						Date appDate2 = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
         						
         						SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
         						String negoDateString = dateFormat.format(tradeProduct.getNegotiationDate());
         						String masterDateString =dateFormat.format(appDate2);
         				        Date negoDate = dateFormat.parse(negoDateString);
         				        Date masterDate = dateFormat.parse(masterDateString);
         				        
         				        System.out.println("masterDate" + masterDate);
         				        System.out.println("negoDate" + negoDate);
         				        
         						if(masterDate.equals(negoDate)){
         							tradeProduct.setAccountStatusId("CURRENT");
         						}
         					} catch (ParseException e) {
         						System.out.println();
         						e.printStackTrace();
         					}
                     	} else if(record.getMaturityDate() != null && !"TF-A".equalsIgnoreCase(record.getAccountStatusId())
                     			&& !"TF-B".equalsIgnoreCase(record.getAccountStatusId())){
                     		Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
                     		if(record.getLastReinstatementDate() != null && record.getNegotiationDate() != null){
                     			if(DATE_COMPARATOR.compare(tradeProduct.getLastReinstatementDate(), tradeProduct.getNegotiationDate()) >= 0){
     	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
     	                        		record.setAccountStatusId("TF-C");
     	                            	tradeProduct.setAccountStatusId("CURRENT");
     	                        	} else if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
     	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
     	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) > 0) {
         	                    		record.setAccountStatusId("TF-A");
         	                        	tradeProduct.setAccountStatusId("EXPIRED");
     	                        	}	                    		
                     			} else if(DATE_COMPARATOR.compare(tradeProduct.getNegotiationDate(), tradeProduct.getLastReinstatementDate()) > 0){
     	                    		if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) == 0){
     	                        		record.setAccountStatusId("TF-C");
     	                            	tradeProduct.setAccountStatusId("CURRENT");
     	                        	} else  if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getNegotiationDate()) < 0
     	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
     	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) > 0) {
         	                    		record.setAccountStatusId("TF-A");
         	                        	tradeProduct.setAccountStatusId("EXPIRED");
     	                        	}	                    		
                     			}
                     		} else if(record.getLastReinstatementDate() != null){
                 				if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) == 0){
 	                        		record.setAccountStatusId("TF-C");
 	                            	tradeProduct.setAccountStatusId("CURRENT");
 	                        	} else if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
 	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
 	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) > 0) {
     	                    		record.setAccountStatusId("TF-A");
     	                        	tradeProduct.setAccountStatusId("EXPIRED");
 	                        	}
                     		} else if(record.getNegotiationDate() != null){
                 				if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) == 0){
 	                        		record.setAccountStatusId("TF-C");
 	                            	tradeProduct.setAccountStatusId("CURRENT");
 	                        	} else  if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getNegotiationDate()) < 0
 	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0
 	                        			&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getNegotiationDate()) > 0) {
     	                    		record.setAccountStatusId("TF-A");
     	                        	tradeProduct.setAccountStatusId("EXPIRED");
 	                        	}
                     		}
                     	}
                     } 

                    if(!"TF-C".equalsIgnoreCase(record.getAccountStatusId())){
                        record.setOutstandingContingentAssets(BigDecimal.ZERO);
                        record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                    }

//            	Calendar cal = Calendar.getInstance();
//                Date tempDate = cal.getTime();
                    Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);

                    if(record.getMaturityDate() != null) {
                          System.out.println("tempdate:" + tempDate + " > maturitydate:" + record.getMaturityDate() + " = " + DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()));
                    }

                    // Below code is unnecessary
                /*
                if(record.getMaturityDate() != null && DATE_COMPARATOR.compare(tempDate,record.getMaturityDate()) > 0) {
                */
                	/*if("CURRENT".equalsIgnoreCase(record.getAccountStatusId()) ||
                			"ACKNOWLEDGED".equalsIgnoreCase(record.getAccountStatusId())||
                			"ACCEPTED".equalsIgnoreCase(record.getAccountStatusId())||
                			"NEGOTIATED".equalsIgnoreCase(record.getAccountStatusId())){*/
                /*
                    if(!"TF311".equalsIgnoreCase(record.getProductId()) &&
             		   !"TF312".equalsIgnoreCase(record.getProductId()) &&
                	   !"TF313".equalsIgnoreCase(record.getProductId()) &&
                	   !"TF314".equalsIgnoreCase(record.getProductId()) &&
                	   !"TF316".equalsIgnoreCase(record.getProductId())){
                		record.setAccountStatusId("CANCELLED");
                		record.setClosedDate(record.getMaturityDate());
                		record.setOutstandingContingentAssets(BigDecimal.ZERO);
                		record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                	}
                }
                */
                }

                //Retrieve from a new table where settlement book code will be stored

                if(DocumentClass.LC.equals(tradeProduct.getDocumentClass()) && DocumentSubType1.REGULAR.equals(tradeProduct.getDocumentSubType1())){
                    if("29".equalsIgnoreCase(record.getSecurityCode()) ){
                        // record.setAppraisal(silverlakeLocalDao.getAppraisalDetails(tradeProduct.getMainCifNumber())); //COMMENT OUT FOR LOCAL
                    }
                    //Appraisal appraisal = silverlakeLocalDao.getAppraisalDetails(tradeProduct.getMainCifNumber()); //COMMENT OUT FOR LOCAL
                }

//            System.out.println(""+tradeProduct.getDocumentClass() +" = "+tradeProduct.getDocumentSubType1() + " = " + tradeProduct.getDocumentSubType2());
                if("USANCE".equalsIgnoreCase(tradeProduct.getDocumentSubType2())){
                       System.out.println("USANCE");
                }

                System.out.println("DOCUMENT NUMBER:"+tradeProduct.getApplicationAccountId()+"|SettlementBookCode:"+tradeProductDao.getSettlementBookCodeByDocumentNumber(tradeProduct.getApplicationAccountId()));
                if(tradeProductDao.getSettlementBookCodeByDocumentNumber(tradeProduct.getApplicationAccountId()) != null){
                    record.setSettlementBlockCode(tradeProductDao.getSettlementBookCodeByDocumentNumber(tradeProduct.getApplicationAccountId()));
                }
                record.setModeOfPayment(tradeProduct.getDocumentClass().getModeOfPayment());
                
                if("TF119".equalsIgnoreCase(record.getProductId()))
                {
                	record.setContingentAssetsGlNumber("-");
                	record.setContingentLiabilitiesGlNumber("-");
                	record.setOutstandingContingentAssets(BigDecimal.ZERO);
                	record.setOutstandingContingentLiabilities(BigDecimal.ZERO);
                }
                
                //TODO UA MATURITY

                record.setCreditFacilityCode(generateCreditFacilityCode(tradeProduct.getMaturityDate(), tradeProduct.getOpenDate()));
                masterFileRecords.add(record);
                System.out.println(record);
            }catch(Exception e){
                System.err.println("DOCUMENT NUMBER " + tradeProduct.getApplicationAccountId() + " WAS REMOVED DUE TO AN ERROR");
                e.printStackTrace();
                continue;
            }
        }

        return masterFileRecords;
    }
    
    private List<TradeProduct> getAllActiveProductsException(String appDate){

        System.out.println("getAllActiveProductsException:"+ appDate);

        // LC
        List<TradeProduct> activeProducts = tradeProductDao.getActiveLettersOfCreditException(appDate);

        // for adding a new record for LC Regular that is adjust to LC Cash
        List<TradeProduct> modifiedLcProduct = new ArrayList<TradeProduct>();
        for(TradeProduct tradeProduct:activeProducts)
        {        	
        	if(tradeProduct.getDocumentSubType1().equals(DocumentSubType1.REGULAR) && tradeProduct.getCashFlag() == 1 && !"USANCE".equalsIgnoreCase(tradeProduct.getDocumentSubType2())){            	
            	BigDecimal originalAmount = tradeProduct.getOriginalAmount();
            	BigDecimal negotiatedAmount = tradeProduct.getTotalNegotiatedAmount();
            	BigDecimal cashAmount = tradeProduct.getCashAmount();
            	cashAmount = cashAmount.subtract(negotiatedAmount);
            	BigDecimal outstandingAmount = originalAmount.subtract(cashAmount);
//				BigDecimal outstandingAmount = originalAmount.subtract(cashAmount);
//            	if(negotiatedAmount.compareTo(BigDecimal.ZERO) > 0){
//            		BigDecimal remainingNegoAmount = cashAmount.subtract(negotiatedAmount);
//            		if(remainingNegoAmount.compareTo(BigDecimal.ZERO) < 0){
//            			remainingNegoAmount = remainingNegoAmount.negate();
//            			cashAmount = BigDecimal.ZERO;
//            		}else{
//            			cashAmount = remainingNegoAmount;
//            		}
//            		outstandingAmount =outstandingAmount.subtract(remainingNegoAmount);
//            	}

            	if(outstandingAmount.compareTo(BigDecimal.ZERO) == -1){
            		outstandingAmount = BigDecimal.ZERO;
            	}

            	if(cashAmount.compareTo(BigDecimal.ZERO) == -1 || (outstandingAmount.compareTo(BigDecimal.ZERO) <= 0 && 
            			originalAmount.compareTo(BigDecimal.ZERO) <= 0 && "CLOSED".equalsIgnoreCase(tradeProduct.getAccountStatusId()))){
            		cashAmount = BigDecimal.ZERO;
            	}

            	tradeProduct.setOutstandingContingentLiabilities(outstandingAmount);
            	tradeProduct.setOutstandingContingentAssets(outstandingAmount);
            	tradeProduct.setContingentAssetsGlNumber(tradeProduct.getPreviousAssetsGlNumber());
            	tradeProduct.setContingentLiabilitiesGlNumber(tradeProduct.getPreviousLiabilitiesGlNumber());
            	tradeProduct.setProductId(tradeProduct.getPreviousProductID());
            	tradeProduct.setCashAmount(cashAmount);
            	

            	if("REINSTATED".equalsIgnoreCase(tradeProduct.getAccountStatusId())){
            		try{
	            		tradeProduct.setAccountStatusId("CURRENT");
	            		Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
	            		//System.out.println("XXXXXXXXXXXXXXXX:::" + tradeProduct.getApplicationAccountId() + "tradeProduct.getMaturityDate():::" + tradeProduct.getMaturityDate() + "tradeProduct.getLastReinstatementDate():::" + tradeProduct.getLastReinstatementDate());
	            		if(DATE_COMPARATOR.compare(tradeProduct.getMaturityDate(), tradeProduct.getLastReinstatementDate()) < 0
	            				&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getLastReinstatementDate()) > 0
	            				&& DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0){
	            			//System.out.println("XXXXXXXXXXXXXXXX:::" + tradeProduct.getApplicationAccountId());
	            			tradeProduct.setOutstandingContingentAssets(BigDecimal.ZERO);
	            			tradeProduct.setOutstandingContingentLiabilities(BigDecimal.ZERO);
	            			tradeProduct.setAccountStatusId("EXPIRED");
	            		}
            		}catch(Exception e){
            			System.out.println();
            			e.printStackTrace();
            		}
            	} else if(("EXPIRED".equalsIgnoreCase(tradeProduct.getAccountStatusId())) && tradeProduct.getNegotiationDate() != null) {
					try {						
						Date appDate2 = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
						String negoDateString = dateFormat.format(tradeProduct.getNegotiationDate());
						String masterDateString =dateFormat.format(appDate2);
				        Date negoDate = dateFormat.parse(negoDateString);
				        Date masterDate = dateFormat.parse(masterDateString);
				        
				        System.out.println("masterDate" + masterDate);
				        System.out.println("negoDate" + negoDate);
				        
						if(masterDate.equals(negoDate)){
							tradeProduct.setAccountStatusId("CURRENT");
						}
					} catch (ParseException e) {
						System.out.println();
						e.printStackTrace();
					}
            	} else if(("OPEN".equalsIgnoreCase(tradeProduct.getAccountStatusId()) || "CANCELLED".equalsIgnoreCase(tradeProduct.getAccountStatusId()))
            			&& tradeProduct.getClosedDate() == null && tradeProduct.getCancelledDate() == null){           		
					try {
						Date tempDate = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(appDate);
						if(DATE_COMPARATOR.compare(tempDate, tradeProduct.getMaturityDate()) > 0){							
							Calendar tempCancelDate = GregorianCalendar.getInstance();
							tempCancelDate.setTime(tradeProduct.getMaturityDate());
							tempCancelDate.add(GregorianCalendar.DATE, +1);
							Date cancelDate = tempCancelDate.getTime();
							
							tradeProduct.setAccountStatusId("CANCELLED");
							tradeProduct.setCancelledDate(cancelDate);
						}
					} catch (ParseException e) {
						System.out.println();
						e.printStackTrace();
					}        		            		
            	}
            	            	
            	//for creating new record with document number for Adjusted Regular to Cash
                TradeProduct tempTradeProduct = tradeProduct.getCloneTradeProduct();            	       	
                StringBuilder applicationAccountId = new StringBuilder(tempTradeProduct.getApplicationAccountId());               
                if(applicationAccountId.toString().startsWith("FX")){
                	applicationAccountId.replace(4,6,"98");
                    tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                    tempTradeProduct.setContingentAssetsGlNumber("-");
                    tempTradeProduct.setContingentLiabilitiesGlNumber("246210101000");
                    tempTradeProduct.setProductId("TF113");
                }else if(applicationAccountId.toString().startsWith("DM")){
                	applicationAccountId.replace(4,6,"99");
                	tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                    tempTradeProduct.setContingentAssetsGlNumber("-");
                    tempTradeProduct.setContingentLiabilitiesGlNumber("246210102000");
                    tempTradeProduct.setProductId("TF217");
                }else{
                    if(tempTradeProduct.getDocumentType().equals(DocumentType.FOREIGN.toString())) {
                    	applicationAccountId.replace(4, 6, "98");
                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                        tempTradeProduct.setContingentAssetsGlNumber("-");
                        tempTradeProduct.setContingentLiabilitiesGlNumber("246210101000");
                        tempTradeProduct.setProductId("TF113");
                    }else{
                    	applicationAccountId.replace(4, 6, "99");
                        tempTradeProduct.setApplicationAccountId(applicationAccountId.toString());
                        tempTradeProduct.setContingentAssetsGlNumber("-");
                        tempTradeProduct.setContingentLiabilitiesGlNumber("246210102000");
                        tempTradeProduct.setProductId("TF217");
                    }
                }
            	tempTradeProduct.setOutstandingContingentLiabilities(cashAmount);
            	tempTradeProduct.setOutstandingContingentAssets(cashAmount);
            	tempTradeProduct.setAccountStatusId("CURRENT");
                modifiedLcProduct.add(tempTradeProduct);// add modified trade product to modifiedLcProduct
                
                if(outstandingAmount.compareTo(BigDecimal.ZERO) == 0 && !"CLOSED".equalsIgnoreCase(tradeProduct.getAccountStatusId())){
            		List<Integer> revIdList = tradeProductDao.getAuditRevId(tradeProduct.getApplicationAccountId(), appDate);
            		Calendar tempClosedDate = GregorianCalendar.getInstance();
            		Boolean isAdjustedToFullCash = false;
            		Boolean isRegularAmountZero = true;
            		BigDecimal lastOutstandingAmountAudit = BigDecimal.ONE; 
            		BigDecimal lastCashAmount = BigDecimal.ONE;
            		int numberOfRevId = revIdList.size();
            		for(Integer revId:revIdList){
            			System.out.println("revId: " + revId + " DocNum: " + tradeProduct.getApplicationAccountId());
            			List<TradeProduct> letterOfCreditAuditList = tradeProductDao.getLetterOfCreditAuditByRevId(revId);
            			
            			Iterator<TradeProduct> letterOfCreditAuditDetails = letterOfCreditAuditList.iterator();
            			while(letterOfCreditAuditDetails.hasNext() && isAdjustedToFullCash == false && isRegularAmountZero == true){
            				TradeProduct letterOfCreditAudit = letterOfCreditAuditDetails.next();
            				BigDecimal auditOriginalAmount = letterOfCreditAudit.getOriginalAmount();
                        	BigDecimal auditNegotiatedAmount = letterOfCreditAudit.getTotalNegotiatedAmount();
                        	BigDecimal auditCashAmount = letterOfCreditAudit.getCashAmount();
                        	auditCashAmount = auditCashAmount.subtract(auditNegotiatedAmount);
                        	BigDecimal auditOutstandingAmount = auditOriginalAmount.subtract(auditCashAmount);
                        	
                        	System.out.println("auditOriginalAmount: " + auditOriginalAmount);
                        	//System.out.println("auditNegotiatedAmount: " + auditNegotiatedAmount);
                        	//System.out.println("auditCashAmount: " + auditCashAmount);
                        	System.out.println("auditOutstandingAmount: " + auditOutstandingAmount);
                        	System.out.println("lastOutstandingAmountAudit: " + lastOutstandingAmountAudit);
                        	System.out.println("lastCashAmount: " + lastCashAmount);
                        	System.out.println("letterOfCreditAudit.getCashAmount(): " + letterOfCreditAudit.getCashAmount());

                        	if(auditOutstandingAmount.compareTo(BigDecimal.ZERO) <= 0 && letterOfCreditAudit.getCashAmount().compareTo(BigDecimal.ZERO) == 1
                        			&& numberOfRevId == 1){           		
                        		isAdjustedToFullCash = true;
                        		tempClosedDate.setTime(letterOfCreditAudit.getClosedDate());
                        	} else if((auditOutstandingAmount.compareTo(BigDecimal.ZERO) == 1 && letterOfCreditAudit.getCashAmount().compareTo(BigDecimal.ZERO) >= 0)
                        			&& (lastOutstandingAmountAudit.compareTo(BigDecimal.ZERO) <= 0 && lastCashAmount.compareTo(BigDecimal.ZERO) == 1)){           		
                        		isAdjustedToFullCash = true;
                        	} else {
                        		lastOutstandingAmountAudit = auditOutstandingAmount;
                        		lastCashAmount = letterOfCreditAudit.getCashAmount();
                        		tempClosedDate.setTime(letterOfCreditAudit.getClosedDate());
                        	}
                        	
                        	if(auditOutstandingAmount.compareTo(BigDecimal.ZERO) == 1 && letterOfCreditAudit.getCashAmount().compareTo(BigDecimal.ZERO) >= 0){
                        		isRegularAmountZero = false;
                        	}
            			}
            		}
            		
            		if(isAdjustedToFullCash) {
						Date closedDate = tempClosedDate.getTime();
						
						tradeProduct.setAccountStatusId("CLOSED");
						tradeProduct.setClosedDate(closedDate);
            		}
            	}
            }
        }       
        //add the trade product with new document number
        activeProducts.addAll(modifiedLcProduct);

        // DA
        activeProducts.addAll(tradeProductDao.getActiveDocumentsAgainstAcceptanceException(appDate));
        System.out.println(activeProducts.size());

        // DP
        activeProducts.addAll(tradeProductDao.getActiveDocumentsAgainstPaymentException(appDate));
        System.out.println(activeProducts.size());

        // EBC,DBC
        activeProducts.addAll(tradeProductDao.getActiveBillsCollectionException(appDate));
        System.out.println(activeProducts.size());
        
        // BG
        activeProducts.addAll(tradeProductDao.getActiveBankGuaranteeException(appDate));
        System.out.println(activeProducts.size());

        System.out.println("with active product size:" + activeProducts.size());

        //TODO:: EXPORTS and CDT

        return activeProducts;
    }
}
