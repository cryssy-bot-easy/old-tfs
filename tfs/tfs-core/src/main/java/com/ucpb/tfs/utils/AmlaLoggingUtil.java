package com.ucpb.tfs.utils;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.ucpb.tfs.domain.service.TradeServiceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * <pre>
 * Program_id    : AmlaLoggingUtil
 * Program_name  : Amla Logging Utility Class
 * SCR_Number    : IBD-12-0502-01
 * Process_Mode  : Online
 * Frequency     : Transactional
 * Input         : N/A
 * Output        : N/A
 * Description   : Utility Class use for AMLA Generation.
 * </pre>
 * @author Val Peacaoco
 */
public class AmlaLoggingUtil {
	
	 @Autowired
	 TradeServiceRepository tradeServiceRepository;
	 

    private static final Currency PHP = Currency.getInstance("PHP");
    public static final int PRECISION = 8;

    public static BigDecimal multiply(BigDecimal multiplicand, BigDecimal multiplier, int precision){
    	
        return multiplicand.multiply(multiplier).setScale(precision, RoundingMode.HALF_UP);
    }

    public static BigDecimal setPrecision(BigDecimal number, int precision){
    	
    	if(number==null) {
    		return new BigDecimal("0.00");
    	}else {
    		return number.setScale(precision,RoundingMode.HALF_UP);
    	}
    	
        
    }

    private static BigDecimal getBigDecimal(String number,int precision){
        return new BigDecimal(number).setScale(precision,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Method use for convertion of Transaction Amount into PHP Value
     * 
     * @param currency LC Currency of Transaction
     * @param amount  Transactional Amount
     * @param rate	  Special Rate use in the transaction
     * @return BigDecimal Returns converted amount into PHP
     */
    public static BigDecimal getTransactionAmount(String currency, String amount, BigDecimal rate){
        if(StringUtil.isEmpty(currency)
                || StringUtil.isEmpty(amount)
                || rate == null){
            return BigDecimal.ZERO;
        }
       
        if(!PHP.equals(Currency.getInstance(currency))){
            return multiply(getBigDecimal(amount.replaceAll(",", ""),PRECISION),rate, 2);
        }
        return getBigDecimal(amount.replaceAll(",", ""), 2);
    }

    /**
     * ethod use for convertion of Transaction Amount into PHP Value in Original Currency
     * 
     * @param amount Transactional Amount
     * @param rate Special Rate use in the transaction
     * @return BigDecimal Returns the amount converted
     */
    
    public static BigDecimal getTransactionAmountInOrigCurrency(String amount, BigDecimal rate){
        if(StringUtil.isEmpty(amount)
                || rate == null){
            return BigDecimal.ZERO;
        }

        	BigDecimal amountToConvert = new BigDecimal(amount.replaceAll(",", ""));
        	BigDecimal amountConverted = amountToConvert.divide(rate, 2,BigDecimal.ROUND_UP);
            return amountConverted;
    }
    
    /**
     * Method use to format Amount by removing all comma(,) with 2 decimal point
     * 
     * @param amount Transactional Amount
     * @return BigDecimal Returns Formatted Amount
     */
    public static BigDecimal formatAmount(String amount){
        if(StringUtil.isEmpty(amount)){
            return BigDecimal.ZERO;
        }

        return getBigDecimal(amount.replaceAll(",", ""),2);
    }

    // explicitly used by Customer Logger
    public static String checkCustomer(String customerType) {
        if ("302".equals(customerType) || "400".equals(customerType)) {
            return "1";
        }
        return "0";
    }

    /**
     * Method use to convert Decimal into Plain String with given precision
     * 
     * @param number     Amount to be converted into String
     * @param precision  Precision set for the amount
     * @return	Returns converted amount into String
     */
    public static String getAmountString(BigDecimal number, int precision){
        if(number == null){
            number = BigDecimal.ZERO;
        }
        return number.setScale(precision,RoundingMode.HALF_UP).toPlainString();
    }
    
    public static String toUpperString(String stringToUpper) {
    	
    	return stringToUpper.toUpperCase();
    }

}
