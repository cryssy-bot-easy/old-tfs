package com.ucpb.tfs.application.query.interfaces;


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
	Project Name: FacilityService.java
*/

public interface FacilityService {

	public List<Map<String,?>> getFacilitiesByCifNumber(String cifNumber);
	
	public List<Map<String,?>> getFacilitiesByChildAndMainCifNumber(String childCifNumber, String mainCifNumber, String facilityType);
	
	public List<Map<String,Object>> getFacilitiesByCifNumberAndType(String cifNumber, String type);

//    public List<Map<String,Object>> getFacilitiesByCifNumberAndFacilityTypes(String cifNumber,String... facilityTypes);
public List<Map<String,Object>> getFacilitiesByCifNumberAndFacilityTypes(String cifNumber,String mainCifNumber,String seqNo, String... facilityTypes);

    public List<Map<String,Object>> getFacilitiesByCifNumberAndFacilityTypesSearch(String cifNumber,
																				   String mainCifNumber,
                                                                                   String seqNo,
                                                                                   String... facilityTypes);


    public Map<String,Object> getFacilityBalance(Long transactionSequenceNumber);

//    public List<Map<String,Object>> getFacilitiesByCifCurrencyAndFacilityTypes(String cifNumber,String currency, String... facilityTypes);
public List<Map<String,Object>> getFacilitiesByCifCurrencyAndFacilityTypes(String cifNumber,String mainCifNumber,String currency,String seqNo, String... facilityTypes);

	public List<Map<String,Object>> getFacilitiesByCifAndFacility(String cifNumber, String facilityRefNo, String facilityType, String facilityId);
}
