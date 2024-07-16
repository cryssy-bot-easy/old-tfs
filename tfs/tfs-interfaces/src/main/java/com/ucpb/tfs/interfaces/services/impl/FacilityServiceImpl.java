package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.domain.Availment;
import com.ucpb.tfs.interfaces.domain.Facility;
import com.ucpb.tfs.interfaces.domain.enums.EarmarkingStatusDescription;
import com.ucpb.tfs.interfaces.repositories.FacilityRepository;
import com.ucpb.tfs.interfaces.services.FacilityService;
import com.ucpb.tfs.interfaces.services.RatesService;
import com.ucpb.tfs.interfaces.services.SequenceService;
import com.ucpb.tfs.interfaces.util.DateUtil;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/*
	(revision)
	SCR/ER Number: ER# 20170109-040
	SCR/ER Description: Transaction allowed to be created even the facility is expired
	[Revised by:] Jesse James Joson
	[Date revised:] 1/17/2017
	Program [Revision] Details: Check the expiry date of the facility before allowing to amend LC
	Member Type: Java
	Project: Core
	Project Name: FacilityServiceImpl.java

    
------------------------------------------------------------------------------------------------------
    Description:    Removed facility codes as parameter on getFacilitiesByType and
                        getFacilitiesByTypeSearch
    Revised by:     Cedrick C. Nungay
    Date revised:   02/26/2024
*/


public class FacilityServiceImpl implements FacilityService{

	private static final String DATE_FORMAT = "MMddyy";
    private static final String PHP = "PHP";
    private static final String USD = "USD";
    private static final String CONVERSION_RATE = "CONVERSION_RATE";
    public static final String BALANCE_INQUIRY_ID = "BALANCE_INQUIRY_ID";
    public static final String CLIENT_CIF_NUMBER = "CLIENT_CIF_NUMBER";
    public static final String FACILITY_TYPE = "FACILITY_TYPE";
    public static final String FACILITY_ID = "FACILITY_ID";
    private FacilityRepository facilityRepository;
    private RatesService ratesService;
    private SequenceService sequenceService;
	
	public List<Map<String, ?>> getFacilitiesByCifNumber(String cifNumber) {
		return facilityRepository.getFacilitiesByCifNumber(cifNumber, getCurrentDateAsString());
	}
	
	public boolean earmarkAvailment(Availment availment) {
        if(!PHP.equalsIgnoreCase(availment.getCurrencyCode())){
            BigDecimal conversionRate = ratesService.getConversionRateByType(availment.getCurrencyCode(),"PHP",3);
            availment.setPhpAmount(availment.getOriginalAmount().multiply(conversionRate));
            availment.setPhpOutstandingBalance(availment.getOutstandingBalance().multiply(conversionRate));
        } else{
            availment.setPhpAmount(availment.getOriginalAmount());
            availment.setPhpOutstandingBalance(availment.getOutstandingBalance());
        }
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii111111111111111  -  earmarkAvailment" + availment.getPhpAmount());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii222222222222222222 - earmarkAvailment" + availment.getPhpOutstandingBalance());

        System.out.println("yosimitsuuuuuuuuuuuuuuuuu" + facilityRepository.checkContingentExists(availment));
        
        if(facilityRepository.checkContingentExists(availment) == 0){
            facilityRepository.insertFacilityAvailment(availment);
            return true;
        }
        else{
            facilityRepository.updateFacilityAvailment(availment);
            return true;
        }
	}

    @Override
    public boolean updateAvailmentAmount(String documentNumber, String currency, BigDecimal amount, Boolean isReinstated) {
        Availment availment = facilityRepository.getAvailment(documentNumber.replaceAll("-",""));
        if(availment != null){
            BigDecimal conversionRate = BigDecimal.ONE;
            if(!"PHP".equals(availment.getCurrencyCode())){
                conversionRate = ratesService.getConversionRateByType(availment.getCurrencyCode(),"PHP",3);
            }

            // availment.setOutstandingBalance(availment.getOutstandingBalance().subtract(amount));
            availment.setOutstandingBalance(amount);
            availment.setPhpOutstandingBalance(availment.getOutstandingBalance().multiply(conversionRate));
            if (isReinstated.equals(Boolean.TRUE)) {
                availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());
            }
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii111111111111111  -  updateAvailmentAmount"+ availment.getOutstandingBalance());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii222222222222222222 - updateAvailmentAmount"+ availment.getPhpOutstandingBalance());

            return facilityRepository.updateFacilityAvailmentBalance(availment) == 1;
        }
        return false;
    }

    @Override
    public boolean updateAvailmentAmountEarmark(String documentNumber, String currency, BigDecimal amount, BigDecimal outstandingBalance, Boolean isReinstated) {
        Availment availment = facilityRepository.getAvailment(documentNumber.replaceAll("-",""));
        if(availment != null){
            BigDecimal conversionRate = BigDecimal.ONE;
            if(!"PHP".equals(availment.getCurrencyCode())){
                conversionRate = ratesService.getConversionRateByType(availment.getCurrencyCode(),"PHP",3);
            }

            BigDecimal availmentAmount = availment.getOutstandingBalance();
            if(availmentAmount.add(amount).compareTo(outstandingBalance) <= 0){
            	availmentAmount = availmentAmount.add(amount);
            } else {
            	availmentAmount = outstandingBalance;
            }
    		availment.setOutstandingBalance(availmentAmount);
            availment.setPhpOutstandingBalance(availmentAmount.multiply(conversionRate));
            if (isReinstated.equals(Boolean.TRUE)) {
                availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());
            }
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii111111111111111  -  updateAvailmentAmountEarmark"+ availment.getOutstandingBalance());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii222222222222222222 - updateAvailmentAmountEarmark"+ availment.getPhpOutstandingBalance());
            return facilityRepository.updateFacilityAvailmentBalance(availment) == 1;
        }
        return false;
    }

    @Override
    public boolean updateAvailmentAmountUnearmark(String documentNumber, String currency, BigDecimal amount, BigDecimal outstandingBalance, Boolean isReinstated, BigDecimal negotiationAmount) {
        Availment availment = facilityRepository.getAvailment(documentNumber.replaceAll("-",""));
        if(availment != null){
            BigDecimal conversionRate = BigDecimal.ONE;
            if(!"PHP".equals(availment.getCurrencyCode())){
                conversionRate = ratesService.getConversionRateByType(availment.getCurrencyCode(),"PHP",3);
            }

            BigDecimal availmentAmount = availment.getOutstandingBalance();
            if(availmentAmount.subtract(amount).compareTo(outstandingBalance.subtract(negotiationAmount)) > 0){
            	if(availmentAmount.subtract(amount).compareTo(BigDecimal.ZERO) >= 0) {
            		availmentAmount = availmentAmount.subtract(amount);
            	} else {
            		availmentAmount = BigDecimal.ZERO;
            	}
            } else if(outstandingBalance.subtract(negotiationAmount).compareTo(BigDecimal.ZERO) >= 0) {
            	availmentAmount = outstandingBalance.subtract(negotiationAmount);
            } else {
            	availmentAmount = BigDecimal.ZERO;
            }
    		availment.setOutstandingBalance(availmentAmount);
            availment.setPhpOutstandingBalance(availmentAmount.multiply(conversionRate));
            if (isReinstated.equals(Boolean.TRUE)) {
                availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());
            }
             System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii111111111111111  -  updateAvailmentAmountUnearmark"+ availment.getOutstandingBalance());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii222222222222222222 - updateAvailmentAmountUnearmark"+ availment.getPhpOutstandingBalance());
            return facilityRepository.updateFacilityAvailmentBalance(availment) == 1;
        }
        return false;
    }

    @Override
    public boolean updateAvailmentCif(String documentNumber, String cifNumber) {
        Availment availment = facilityRepository.getAvailment(documentNumber.replaceAll("-",""));
        if (availment != null) {
            availment.setCifNumber(cifNumber);
            availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii111111111111111  -  updateAvailmentCif" + availment.getOutstandingBalance());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii222222222222222222 - updateAvailmentCif" + availment.getPhpOutstandingBalance());
            return facilityRepository.updateFacilityAvailmentCif(availment) == 1;
        }
        return false;
    }

    @Override
    public boolean updateAvailmentFacilityReferenceNumber(String documentNumber, String facilityReferenceNumber) {
        Availment availment = facilityRepository.getAvailment(documentNumber.replaceAll("-",""));
        if (availment != null) {
            availment.setFacilityReferenceNumber(facilityReferenceNumber);
            availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii111111111111111  -  updateAvailmentFacilityReferenceNumber" + availment.getOutstandingBalance());
            System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii222222222222222222 - updateAvailmentFacilityReferenceNumber" + availment.getPhpOutstandingBalance());
            return facilityRepository.updateFacilityAvailmentFacilityReferenceNumber(availment) == 1;
        }
        return false;
    }

    public boolean unearmarkAvailment(String documentNumber) {
    	try{
          
        System.out.println("\n::::::::::::::::::  UN-EARMARK AVAILMENT ::::::::::::::::::");
		return facilityRepository.deleteFacilityAvailment(documentNumber.replaceAll("-","")) > 0;
    	}catch(Exception e){
   		 e.printStackTrace();
   	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
           
   	}
		
	}

    public Long insertFacilityBalanceQuery(Facility facility) {
        Long sequenceNumber = sequenceService.getFacilityBalanceSequence();
        System.out.println("arrrrrrrruuuuuuyyyyyyyyyyyyy: " + sequenceNumber);
        int insertCheck = facilityRepository.insertFacilityBalanceQuery(facility,sequenceNumber);
        System.out.println("insertCheck yesssssssssssssss: " + insertCheck);
        int loopCheck = 0;
        while(insertCheck == 0 && loopCheck <= 9){
        	try{
	        	this.wait(500);
	        	insertCheck = facilityRepository.insertFacilityBalanceQuery(facility,sequenceNumber);
	        	System.out.println("insertCheck: " + insertCheck);
	        	loopCheck++;
        	} catch(Exception e){
        		break;
        	}
        }
        return sequenceNumber;
    }

    @Override
    public Map<String, Object> getFacilityBalance(@Param("transactionSequenceNo") Long transactionSequenceNumber) {
        return facilityRepository.getFacilityBalance(transactionSequenceNumber);
    }

//    public List<Map<String, Object>> getFacilitiesByCifNumberAndFacilityTypes(String cifNumber, String... facilityTypes) {
    @Override
    public List<Map<String, Object>> getFacilitiesByCifNumberAndFacilityTypes(String cifNumber, String mainCifNumber, String seqNo, String... facilityTypes) {

        List<Map<String,Object>> facilities =  facilityRepository.getFacilitiesByType(cifNumber, mainCifNumber, seqNo);
//        for(Map<String,Object> facility : facilities){
//            Long transactionSequenceNumber = sequenceService.getFacilityBalanceSequence();
//            facilityRepository.insertFacilityBalanceQuery(mapToFacility(facility),transactionSequenceNumber);
//            facility.put(BALANCE_INQUIRY_ID,transactionSequenceNumber);
//        }
        return facilities;
    }

    @Override
    public List<Map<String, Object>> getFacilitiesByCifNumberAndFacilityTypesSearch(String cifNumber,
                                                                                    String mainCifNumber,
                                                                                    String seqNo,
                                                                                    String... facilityTypes) {

        List<Map<String,Object>> facilities =  facilityRepository.getFacilitiesByTypeSearch(cifNumber,
                mainCifNumber,
                seqNo);
//        for(Map<String,Object> facility : facilities){
//            Long transactionSequenceNumber = sequenceService.getFacilityBalanceSequence();
//            facilityRepository.insertFacilityBalanceQuery(mapToFacility(facility),transactionSequenceNumber);
//            facility.put(BALANCE_INQUIRY_ID,transactionSequenceNumber);
//        }
        return facilities;
    }

//    public List<Map<String, Object>> getFacilitiesByCifCurrencyAndFacilityTypes(String cifNumber, String currency, String... facilityTypes) {
    @Override
    public List<Map<String, Object>> getFacilitiesByCifCurrencyAndFacilityTypes(String cifNumber, String mainCifNumber, String currency, String seqNo, String... facilityTypes) {
        return facilityRepository.getFacilitiesByTypeAndCurrency(cifNumber,mainCifNumber,currency + "%",seqNo, facilityTypes);
    }


    public void setFacilityRepository(FacilityRepository facilityRepository) {
		this.facilityRepository = facilityRepository;
	}

	public List<Map<String, Object>> getFacilitiesByCifNumberAndType(String cifNumber, String type) {
		List<Map<String,Object>> facilities = facilityRepository.getFacilitiesByCifNumberAndType(cifNumber,  getCurrentDateAsString(), type);
//        for(Map<String,Object> facility : facilities){
//            Long transactionSequenceNumber = sequenceService.getFacilityBalanceSequence();
//            facilityRepository.insertFacilityBalanceQuery(mapToFacility(facility),transactionSequenceNumber);
//            facility.put(BALANCE_INQUIRY_ID,transactionSequenceNumber);
//        }
        return facilities;
	}

	@Override
	public List<Map<String, ?>> getFacilitiesByChildAndMainCifNumber(String childCifNumber, String mainCifNumber,String facilityType) {
		return facilityRepository.getFacilitiesByChildAndMainCifNumber(childCifNumber, mainCifNumber, getCurrentDateAsString(), facilityType);
	}
	
    private String getCurrentDateAsString(){
        return DateUtil.formatToString(DATE_FORMAT, new Date());
    }

    public void setSequenceService(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    public void setRatesService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    private Facility mapToFacility(Map<String,Object> facility){
        Facility mappedFacility = new Facility();
        mappedFacility.setCifNumber((String)facility.get(CLIENT_CIF_NUMBER));
        mappedFacility.setFacilityType((String)facility.get(FACILITY_TYPE));
        if(facility.get(FACILITY_ID) != null){
            mappedFacility.setFacilityId(((BigDecimal)facility.get(FACILITY_ID)).intValue());
        }
        return mappedFacility;
    }

    public List<Map<String,Object>> getFacilitiesByCifAndFacility(String cifNumber, String facilityRefNo, String facilityType, String facilityId) {
    	List<Map<String,Object>> facilities = facilityRepository.getFacilitiesByCifAndFacility(cifNumber,  facilityRefNo, facilityType, facilityId);
    	return facilities;
    
	}

}
