package com.ucpb.tfs.interfaces.repositories;

import java.util.List;
import java.util.Map;

import com.ucpb.tfs.interfaces.domain.AllocationUnit;
import com.ucpb.tfs.interfaces.domain.CustomerInformationFile;
import org.apache.ibatis.annotations.Param;

public interface CustomerInformationFileRepository {
	
	public Map<String,Object> getCifByCifInCDT(@Param("cifNumber")String cifNumber);
	
	public Map<String,Object> getCifByCifNumber(@Param("cifNumber")String cifNumber);

	public List<Map<String,Object>> getMainCifsByClientCifNumber(@Param("clientCifNumber")String clientCifNumber, @Param("expiryDate") String expiryDate);
	
	public List<Map<String,Object>> getCifsByCifName(@Param("cifName") String cifName);

    public List<Map<String,Object>> getCifsByNameAndNumber(@Param("cifName")String cifName, @Param("cifNumber")String cifNumber);

    public List<Map<String,Object>> getCifsByNameOrNumber(@Param("cifName")String cifName, @Param("cifNumber")String cifNumber);

    public List<Map<String,Object>> getChildCifsByMainCifNumber(@Param("mainCifNumber")String mainCifNumber, @Param("expiryDate")String expiryDate);

    public List<Map<String,Object>> getCasaAccounts(@Param("cifNumber")String cifNumber);

    public List<Map<String,Object>> getCasaAccountsByCifNumberAndCurrency(@Param("cifNumber")String cifNumber, @Param("currency")String currency);

    public AllocationUnit getBranchUnitCodeForAlphanumericOfficerCode(@Param("officerCode") String officerCode);

    public AllocationUnit getBranchUnitCodeForAlphaOfficerCode(@Param("officerCode") String officerCode);

    public AllocationUnit getBranchUnitCodeForNumericOfficerCode(@Param("officerCode")String officerCode);

    public List<Map<String,Object>> getCasaAccountsByNumberAndCurrency(@Param("accountNumber")String accountNumber, @Param("currency")String currency);
    
    public List<Map<String,Object>> getCifFullNameByCifName(@Param("cifName")String cifName);
    
    public List<Map<String,Object>> getCifsByNameAndOrNumber(@Param("cifName")String cifName, @Param("cifNumber")String cifNumber, @Param("branchUnitCode")String branchUnitCode);

    public CustomerInformationFile getCifDetailsByCifNumber(@Param("cifNumber")String cifNumber);

    public Map<String, Object> getCifByTinNumber(@Param("tinNumber") String tinNumber);
}
