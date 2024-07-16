package com.ucpb.tfs.application.query.interfaces;

import java.util.List;
import java.util.Map;

public interface CustomerInformationFileService {

    public Map<String,?> getCifByCifNumber(String cifNumber);

    public List<Map<String,?>> getMainCifsByClientCifNumber(String childCifNumber);

    public List<Map<String,?>> getCifsByCifName(String cifName);

    public List<Map<String,Object>> getCifsByNameAndNumber(String cifName, String cifNumber,String branchUnitCode);

    public List<Map<String,?>> getChildCifsByMainCifNumber(String mainCifNumber);

    public List<Map<String,?>> getCasaAccounts(String cifNumber);

    public List<Map<String,Object>> getCasaAccountsByCifNumberAndCurrency(String cifNumber,String currency);
    
    public Map<String,Object> getCifsByNameAndOrNumber(String cifName, String cifNumber,String branchUnitCode);

    public Map<String, Object> getCifByTinNumber(String tinNumber);
    
    public Map<String, Object> getCifByCifInCDT(String cifNumber);

}

