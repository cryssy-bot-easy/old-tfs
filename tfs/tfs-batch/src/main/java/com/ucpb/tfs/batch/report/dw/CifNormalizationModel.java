package com.ucpb.tfs.batch.report.dw;

import java.util.HashMap;
import java.util.Map;

/**PROLOGUE:
 * 	(revision)
	SCR/ER Number: ERF# 20140909-038
	SCR/ER Description: CIF Normalization Not Working in TFS
	[Revised by:] Jesse James Joson
	[Date Created:] 08/05/2016
	Program [Revision] Details: The CIF Normalization was redesigned, since not all tables are normalized.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CifNormalizationModel
 *
 */


public class CifNormalizationModel {

	private String tradeServiceId;
	private String serviceInstructionId;
	private String documentNumber;
	private String cifNumber;
	private String mainCifNumber;
	private String facilityId;
    private String facilityType;
    private Map<String, Object> details;
    private String detailsStr;
    
    
	public Map<String, Object> getDetails() {
		return details;
	}
	public void setDetails(Map<String, Object> details) {
		this.details = details;
	}
	public String getDetailsStr() {
		return detailsStr;
	}
	public void setDetailsStr(String detailsStr) {
		this.detailsStr = detailsStr;
	}
	public String getTradeServiceId() {
		return tradeServiceId;
	}
	public void setTradeServiceId(String tradeServiceId) {
		this.tradeServiceId = tradeServiceId;
	}
	public String getServiceInstructionId() {
		return serviceInstructionId;
	}
	public void setServiceInstructionId(String serviceInstructionId) {
		this.serviceInstructionId = serviceInstructionId;
	}
	public String getCifNumber() {
		return cifNumber;
	}
	public void setCifNumber(String cifNumber) {
		this.cifNumber = cifNumber;
	}
	public String getMainCifNumber() {
		return mainCifNumber;
	}
	public void setMainCifNumber(String mainCifNumber) {
		this.mainCifNumber = mainCifNumber;
	}
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	public String getFacilityType() {
		return facilityType;
	}
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
    
	public void generateDetailsMap() {
		String str = this.detailsStr;
		Map<String, Object> tempMap = new HashMap<String, Object>();
		
		if (!returnIfNull(detailsStr).equalsIgnoreCase("") && !detailsStr.equalsIgnoreCase("{}")) {
			//System.out.println("String Details\t" + detailsStr);
			str = str.substring(1, str.length()-1);	
			str = str.replace("\",\"", "\"\",\"\"");
			String[] token = str.split("\",\"");
					
			for(int i=0; i < token.length; i++ ) {
				int counter = 0;
				for(int x=0 ; x<token[i].length()-1 ; x++){
					if((token[i].charAt(x)=='\"') && (token[i].charAt(x+1)==':')){
						counter++;
					}
				}
				if(counter>1) {
					str = token[i];
					str = str.replace(",\"", ",\"\"");
					String[] token2 = str.split(",\"");
					for(int i2=0; i2 < token2.length; i2++ ) {
						token2[i2] = token2[i2].replace("\":", "\"\":");
						String[] value2 = token2[i2].split("\":",2);
						value2[1] = value2[1].replace("\"\":", "\":");
						tempMap.put(value2[0].toString(), value2[1].toString());
					}
					
				} else if (counter==1) {
					token[i] = token[i].replace("\":", "\"\":");
					String[] value = token[i].split("\":",2);
					tempMap.put(value[0].toString(), value[1].toString());
				}
				

			}
			
			this.details = tempMap;
		} else {
			this.details = null;
		}
		
	}
	
	public void generateServiceDetailsMap() {
		String str = this.detailsStr;
		Map<String, Object> tempMap = new HashMap<String, Object>();
		
		if (!returnIfNull(detailsStr).equalsIgnoreCase("") && !detailsStr.equalsIgnoreCase("{}")) {
			//System.out.println("String Details\t" + detailsStr);
			str = str.substring(1, str.length()-1);	
			str = str.replace("\",\"", "\"\",\"\"");
			String[] token = str.split("\",\"");
					
			for(int i=0; i < token.length; i++ ) {
				int counter = 0;
				for(int x=0 ; x<token[i].length()-1 ; x++){
					if((token[i].charAt(x)=='\"') && (token[i].charAt(x+1)==':')){
						counter++;
					}
				}
				if(counter>1) {
					str = token[i];
					str = str.replace(",\"", ",\"\"");
					String[] token2 = str.split(",\"");
					for(int i2=0; i2 < token2.length; i2++ ) {
						token2[i2] = token2[i2].replace("\":", "\"\":");
						String[] value2 = token2[i2].split("\":",2);
						value2[1] = value2[1].replace("\"\":", "\":");
						tempMap.put(value2[0].toString(), value2[1].toString());
					}
					
				} else if (counter==1) {
					token[i] = token[i].replace("\":", "\"\":");
					String[] value = token[i].split("\":",2);
					tempMap.put(value[0].toString(), value[1].toString());
				}
				

			}
			
			this.details = tempMap;
		} else {
			this.details = null;
		}
		
	}
	
	public void saveDetails(Map<String, Object> detailsMap) {
		this.detailsStr = detailsMap.toString();
		this.detailsStr = this.detailsStr.replace("\"=\"", "\":\"");
		this.detailsStr = this.detailsStr.replace("\", \"", "\",\"");
		//System.out.println("DetailsStr after.. " + this.detailsStr);
	}
	
	public String returnIfNull(String str) {
		if (str==null) {
			str = "";
		}
		return str;
	}
}
