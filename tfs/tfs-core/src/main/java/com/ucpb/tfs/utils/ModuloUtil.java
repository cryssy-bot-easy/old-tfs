package com.ucpb.tfs.utils;

import org.apache.commons.lang.StringUtils;

public class ModuloUtil {

	private ModuloUtil(){
		//do not instantiate
	}
	
	public static long getCheckDigit(long number){
		long check = getWeightedSum(number,2) % 11;
		return (check == 1) ? 0 : 11 - check;
	}
	
	public static long getCheckDigit(String number){
		return getCheckDigit(Long.parseLong(number));
	}
	
	public static long getCheckDigit(String number, String separator){
		return getCheckDigit(Long.parseLong(StringUtils.remove(number, separator)));
	}
	
	public static boolean isCheckDigitValid(long number, int checkDigit){
		return ((getWeightedSum(number, 2) + checkDigit) % 11) == 0;
	}
	
	public static long getWeightedSum(long number, int multiplier){
		if(number == 0){
			return 0;
		}
		long lastDigit = number % 10;
		return (lastDigit * multiplier) + getWeightedSum((number / 10),nextValue(multiplier));
	}
	
	private static int nextValue(int multiplier){
		if(multiplier == 7){
			return 2;
		}
		return ++multiplier;
	}
	
}
