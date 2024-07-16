package com.ucpb.tfs.interfaces.services.impl;

import java.util.*;

import com.ucpb.tfs.interfaces.domain.AllocationUnit;
import com.ucpb.tfs.interfaces.domain.CustomerInformationFile;
import com.ucpb.tfs.interfaces.domain.enums.Currency;
import com.ucpb.tfs.interfaces.repositories.CustomerInformationFileRepository;
import com.ucpb.tfs.interfaces.services.CustomerInformationFileService;
import com.ucpb.tfs.interfaces.util.DateUtil;
import org.apache.commons.lang.StringUtils;

public class CustomerInformationFileServiceImpl implements CustomerInformationFileService{

    private static final String DATE_FORMAT = "MMddyy";
    public static final String BRANCH_UNIT_CODE = "BRANCH_UNIT_CODE";
    public static final String ALLOCATION_UNIT_CODE = "ALLOCATION_UNIT_CODE";
    public static final String OFFICER_CODE = "OFFICER_CODE";
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    private CustomerInformationFileRepository customerInformationFileRepository;
	
    public Map<String, Object> getCifByCifInCDT(String cifNumber){
    	System.out.println("TFSCORE1234567890" + cifNumber);
    	Map<String, Object> cif = customerInformationFileRepository.getCifByCifInCDT(cifNumber);
    	return cif;
    }
    
	public Map<String, Object> getCifByCifNumber(String cifNumber) {
        Map<String, Object> cif = customerInformationFileRepository.getCifByCifNumber(cifNumber);
        AllocationUnit unitCode = getUnitCode((String) cif.get(OFFICER_CODE));
        cif.put(BRANCH_UNIT_CODE, unitCode.getCcbdBranchUnitCode());
        cif.put(ALLOCATION_UNIT_CODE,unitCode.getAllocationUnitCode());
        return cif;
    }

	public List<Map<String, Object>> getMainCifsByClientCifNumber(
			String childCifNumber) {
		List<Map<String,Object>> cifs = customerInformationFileRepository.getMainCifsByClientCifNumber(childCifNumber,DateUtil.formatToString(DATE_FORMAT, new Date()));

        /*for(Map<String,Object> cif : cifs){
            AllocationUnit unitCode = getUnitCode((String) cif.get(OFFICER_CODE));
            cif.put(BRANCH_UNIT_CODE,unitCode.getCcbdBranchUnitCode());
            cif.put(ALLOCATION_UNIT_CODE,unitCode.getAllocationUnitCode());
        }*/
        return cifs;
	}
	
	public List<Map<String, Object>> getCifsByCifName(String cifName) {
        List<Map<String,Object>> cifs = customerInformationFileRepository.getCifsByCifName("%" + cifName + "%");
        for(Map<String,Object> cif : cifs){
            AllocationUnit unitCode = getUnitCode((String) cif.get(OFFICER_CODE));
            cif.put(BRANCH_UNIT_CODE, unitCode.getCcbdBranchUnitCode());
            cif.put(ALLOCATION_UNIT_CODE,unitCode.getAllocationUnitCode());
        }
        return cifs;
	}

    @Override
    public List<Map<String, Object>> getCasaAccountsByCurrency(String cifNumber, String currency) {
//        return getMatchingCurrencyAccounts(currency,customerInformationFileRepository.getCasaAccounts(cifNumber));
        List<Map<String, Object>> dummyList = new ArrayList<Map<String, Object>>();
        return dummyList;
    }

    @Override
    public List<Map<String, Object>> getCifsByNameAndNumber(String cifName, String cifNumber, String branchUnitCode) {
        List<Map<String,Object>> cifs = null;
        if(StringUtils.isBlank(cifName) || StringUtils.isBlank(cifNumber)){
            cifs = customerInformationFileRepository.getCifsByNameOrNumber(toUpper(cifName),toUpper(cifNumber));
        }else{
            cifs = customerInformationFileRepository.getCifsByNameAndNumber(toUpper(cifName),toUpper(cifNumber));
        }

        /*for(Map<String,Object> cif : cifs){
            AllocationUnit unitCode = getUnitCode((String) cif.get(OFFICER_CODE));
            cif.put(BRANCH_UNIT_CODE, unitCode.getCcbdBranchUnitCode());
            cif.put(ALLOCATION_UNIT_CODE,unitCode.getAllocationUnitCode());
        }*/
        for (Iterator<Map<String,Object>> iterator = cifs.iterator(); iterator.hasNext();){
        	Map<String,Object> cif = iterator.next();
        	AllocationUnit unitCode = getUnitCode((String) cif.get(OFFICER_CODE));
            if(branchUnitCode.equals("909") || branchUnitCode.equals(unitCode.getCcbdBranchUnitCode())){
	            cif.put(BRANCH_UNIT_CODE, unitCode.getCcbdBranchUnitCode());
	            cif.put(ALLOCATION_UNIT_CODE,unitCode.getAllocationUnitCode());
            } else {
            	iterator.remove();
            }
        }
        
        return cifs;
    }

    @Override
    public List<Map<String, Object>> getChildCifsByMainCifNumber(String mainCifNumber) {
        List<Map<String,Object>> cifs = customerInformationFileRepository.getChildCifsByMainCifNumber(mainCifNumber,DateUtil.formatToString(DATE_FORMAT, new Date()));
        for(Map<String,Object> cif : cifs){
            AllocationUnit unitCode = getUnitCode((String) cif.get(OFFICER_CODE));
            cif.put(BRANCH_UNIT_CODE, unitCode.getCcbdBranchUnitCode());
            cif.put(ALLOCATION_UNIT_CODE,unitCode.getAllocationUnitCode());
        }
        return cifs;
    }

    @Override
    public List<Map<String, Object>> getCasaAccounts(String cifNumber) {
        return customerInformationFileRepository.getCasaAccounts(cifNumber);
    }

    @Override
    public List<Map<String, Object>> getCasaAccountsByCifNumberAndCurrency(String cifNumber, String currency) {
        System.out.println("getCasaAccountsByCifNumberAndCurrency");
//        List<Map<String,Object>> accounts = customerInformationFileRepository.getCasaAccountsByCifNumberAndCurrency(cifNumber,currency);
//
//        System.out.println("cifNumber: " + cifNumber);
//        System.out.println("currency: " + currency);
//
//
//        System.out.println("accountNumbers:");
//        for(Map<String,Object> accountDetails : accounts){
//            System.out.println(accountDetails);
//        }
//
//        for(Map<String,Object> accountDetails : accounts){
//            accountDetails.put("ACCOUNT_NUMBER",String.format("%1$012.0f",accountDetails.get("ACCOUNT_NUMBER")));
//        }
//        return accounts;
        List<Map<String,Object>> accounts = new ArrayList<Map<String, Object>>();
        return accounts;
    }

    public void setCustomerInformationFileRepository(CustomerInformationFileRepository cifRepository) {
		this.customerInformationFileRepository = cifRepository;
	}

    private String toUpper(String value){
        if(!StringUtils.isBlank(value)){
            return "%" + value.toUpperCase() + "%";
        }
        return "";
    }

    private AllocationUnit getUnitCode(String officerCode){
        AllocationUnit allocationUnit = null;
        if(StringUtils.isNumeric(officerCode)){
            allocationUnit = customerInformationFileRepository.getBranchUnitCodeForNumericOfficerCode(officerCode);
        }else{
            allocationUnit = customerInformationFileRepository.getBranchUnitCodeForAlphanumericOfficerCode(officerCode);
            if(allocationUnit == null){
                allocationUnit = customerInformationFileRepository.getBranchUnitCodeForAlphaOfficerCode(officerCode);
            }
        }

        if(allocationUnit != null){
            return allocationUnit;
        }
        return new AllocationUnit();
    }

    private List<Map<String,Object>> getMatchingCurrencyAccounts(String currency, List<Map<String, Object>> casaAccounts){
        List<Map<String,Object>> matchingCurrencies = new ArrayList<Map<String, Object>>();
        String casaCurrencyCode = Currency.getCasaCurrencyCode(currency);
        if(casaCurrencyCode != null){
            for(Map<String,Object> account : casaAccounts){
                String accountNumber = account.get(ACCOUNT_NUMBER).toString();
                if(casaCurrencyCode.equalsIgnoreCase(getCurrencyFromAccountNumber(accountNumber))){
                    matchingCurrencies.add(account);
                }
            }
        }
        return matchingCurrencies;
    }

    private String getCurrencyFromAccountNumber(String accountNumber){
        if(accountNumber.matches("\\d{12}")){
            return String.valueOf(accountNumber.charAt(1));
        }
        return null;
    }

	@Override
	public List<Map<String, Object>> getCasaAccountsByNumberAndCurrency(String accountNumber, String currency) {
//		List<Map<String,Object>> accounts = customerInformationFileRepository.getCasaAccountsByNumberAndCurrency(accountNumber, currency);
        List<Map<String,Object>> accounts = new ArrayList<Map<String, Object>>();
		return accounts;
	}

	@Override
	public List<Map<String, Object>> getCifsByNameAndOrNumber(String cifName, String cifNumber, String branchUnitCode) {
		System.out.println("cifName: " + toUpper(cifName));
		System.out.println("cifNumber: " + toUpper(cifNumber));
		System.out.println("branchUnitCode: " + branchUnitCode);
		List<Map<String,Object>> cifs = customerInformationFileRepository.getCifsByNameAndOrNumber(toUpper(cifName), toUpper(cifNumber), branchUnitCode);
		return cifs;
	}

    @Override
    public Date getBirthdayByCifNumber(String cifNumber) {
        CustomerInformationFile cif =  customerInformationFileRepository.getCifDetailsByCifNumber(cifNumber);
        if(cif == null){
            throw new IllegalArgumentException("CIF Number: " + cifNumber + " does not exist!");
        }
        return cif.getFormattedIncorporationDate();
    }

    @Override
    public Map<String, Object> getCifByTinNumber(String tinNumber) {
        Map<String, Object> cifMap = customerInformationFileRepository.getCifByTinNumber(tinNumber);

        if (cifMap != null) {
            AllocationUnit unitCode = getUnitCode((String) cifMap.get("OFFICER_CODE"));
            cifMap.put(BRANCH_UNIT_CODE, unitCode.getCcbdBranchUnitCode());
            cifMap.put(ALLOCATION_UNIT_CODE,unitCode.getAllocationUnitCode());
        }

        return cifMap;
    }

	@Override
	public List<Map<String, Object>> getCifFullNameByCifName(String cifName) {
		List<Map<String,Object>> accounts = customerInformationFileRepository.getCifFullNameByCifName(cifName);
		return accounts;
	}
}
