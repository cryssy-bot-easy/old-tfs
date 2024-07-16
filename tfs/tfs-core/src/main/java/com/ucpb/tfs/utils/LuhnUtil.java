package com.ucpb.tfs.utils;

import org.apache.commons.lang.StringUtils;

public class LuhnUtil {

	public static long getCheckSum(String number){
		long sum = 0;
        char[] digits = StringUtils.reverse(number).toCharArray();
        for(int ctr = 0; ctr < digits.length; ctr++){
            int digit = Character.getNumericValue(digits[ctr]);
            sum += (ctr % 2 == 0) ? ((digit*2) % 10) + ((digit*2)/10) : digit;
        }
		return sum;
	}
	
	public static long getCheckDigit(String number){
		return (getCheckSum(number) * 9) % 10;
	}

    public static long getCheckDigit(String number, String separator){
        return getCheckDigit(StringUtils.remove(number, separator));
    }
	
	public static boolean isNumberValid(String number){
        long checkSum = getCheckSum(number.substring(0,number.length()-1));
        int checkDigit = Integer.valueOf(number.substring(number.length()-1,number.length()));
		return (checkSum + checkDigit) % 10 == 0;
	}

	
}
