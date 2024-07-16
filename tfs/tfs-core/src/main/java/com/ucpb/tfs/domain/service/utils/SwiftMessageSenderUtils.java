package com.ucpb.tfs.domain.service.utils;

import org.apache.commons.lang.StringUtils;

public class SwiftMessageSenderUtils {
	private static final String EMPTY = "";
	
	public static synchronized boolean isNotEmpty(Object... objects){
		boolean result = false;
		
		for(Object obj: objects){
			if(!getStringValue(obj).equals(EMPTY)){
				result=true;
			}
		}
		
		return result;
	}
	
	public static synchronized String getExistingValue(Object... objects){
		String result = EMPTY;
		String temp;
		
		for(Object obj: objects){
			temp = getStringValue(obj); 
			if(!temp.equals(EMPTY)){
				result = temp;
				break;
			}
		}
		
		return result;
	}
	
	
	private static String getStringValue(Object obj){
		if(obj != null){
			return obj.toString().trim();
		}else{
			return EMPTY;
		}
	}
	
	@Deprecated
	public static String getExistingBank(Object bankTo, Object bankFrom){
		String result=StringUtils.EMPTY;
		if(bankTo != null && !StringUtils.isBlank(bankTo.toString())){
			result=bankTo.toString();
		}else if(bankFrom != null && !StringUtils.isBlank(bankFrom.toString())){
			result=bankFrom.toString();
		}
		return result;
	}
}
