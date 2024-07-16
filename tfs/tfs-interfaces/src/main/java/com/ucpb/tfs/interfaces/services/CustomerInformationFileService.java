package com.ucpb.tfs.interfaces.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CustomerInformationFileService {

	public Map<String,Object> getCifByCifNumber(String cifNumber);
	
	public List<Map<String,Object>> getMainCifsByClientCifNumber(String childCifNumber);
	
	public List<Map<String,Object>> getCifsByCifName(String cifName);

    public List<Map<String,Object>> getCasaAccountsByCurrency(String cifNumber,String currency);

    public List<Map<String, Object>> getCifsByNameAndNumber(String cifName, String cifNumber, String branchUnitCode);

    public List<Map<String,Object>> getChildCifsByMainCifNumber(String mainCifNumber);

    public List<Map<String,Object>> getCasaAccounts(String cifNumber);

    public List<Map<String,Object>> getCasaAccountsByCifNumberAndCurrency(String cifNumber,String currency);
    
    public List<Map<String,Object>> getCasaAccountsByNumberAndCurrency(String accountNumber, String currency);
    
    public List<Map<String,Object>> getCifFullNameByCifName(String cifName);
    
    public List<Map<String, Object>> getCifsByNameAndOrNumber(String cifName, String cifNumber, String branchUnitCode);

    public Date getBirthdayByCifNumber(String cifNumber);
    public Map<String, Object> getCifByTinNumber(String tinNumber);
}
