package com.ucpb.tfs.utils;

import com.ucpb.tfs.domain.condition.AdditionalCondition;
import com.ucpb.tfs.domain.condition.LcAdditionalCondition;
import com.ucpb.tfs.domain.documents.RequiredDocument;
import com.ucpb.tfs.domain.reimbursing.InstructionToBank;
import com.ucpb.tfs.domain.reimbursing.LcInstructionToBank;
import com.ucpb.tfs.domain.swift.SwiftCharge;
import com.ucpb.tfs.swift.message.writer.DefaultSwiftMessageWriter;
import com.ucpb.tfs.util.SwiftUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Description: RM 6846 - Error on MT 700 format for field 45A (Description of Goods and/or Services)
	[Date Revised:] 05/24/2018
	Program [Revision] Details: Include multiline formatter in description of goods
	PROJECT: tfs
	MEMBER TYPE  : Java
	Project Name: SwiftFormatter
 */
public class SwiftFormatter {

    private static final String EMPTY = "";
    private static final String SPACE = " ";
    
    public static String formatConditions(Set<AdditionalCondition> conditions){
        if(conditions == null){
        	return EMPTY;
        }
    	
    	StringBuilder sb = new StringBuilder();
        for(AdditionalCondition condition : conditions){
            sb.append("\r\n+").append(condition.getCondition());
        }

        return SwiftUtil.limitInputString(65, sb.toString());
    }
    
    public static String formatDocument(Set<RequiredDocument> conditions){
        if(conditions == null){
        	return EMPTY;
        }
    	
    	StringBuilder sb = new StringBuilder();
        for(RequiredDocument condition : conditions){
            sb.append("\r\n+").append(condition.getDescription());
        }

        return SwiftUtil.limitInputString(65, sb.toString());
    }
// -- Bug 3186 Start--
//    public static String formatDescriptionOfGoods(String... descriptions){
//    	if(descriptions == null){
//    		return EMPTY;
//    	}
//    	
//    	StringBuilder sb = new StringBuilder();
//    	for(String description : descriptions){
//    		if(description == null) {
//    			continue;
//    		}
//    		sb.append("+").append(description);
//    	}
//    	
//    	return SwiftUtil.formatMultiLine(sb.toString(), 65);
//    }
    
    public static String formatDescriptionOfGoods(String descriptionOfGoods, String priceTerm){
    	StringBuilder sb = new StringBuilder();
    	sb.append("+"+descriptionOfGoods).append("\r\n+"+priceTerm);
    	//return sb.toString();
    	return SwiftUtil.limitInputString(65, sb.toString());
    }
//-- Bug 3186 End--

    public static String formatInstructionsToBank(Set<InstructionToBank> instructionsToBank){
    	if(instructionsToBank ==null){
    		return EMPTY;
    	}
    	
        StringBuilder sb = new StringBuilder();
        for(InstructionToBank instruction : instructionsToBank){
            sb.append("+").append(instruction.getInstruction()).append(SPACE);
        }
        return SwiftUtil.limitInputString(65, sb.toString());
    }
    
    public static String formatInstructionsToBankForField78(Set<InstructionToBank> instructionsToBank){
        
        StringBuilder sb = new StringBuilder();
        String defaultInstruction = "+ALL OTHER TERMS AND CONDITIONS REMAIN UNCHANGED.";
        
        if(instructionsToBank == null){
            sb.append(defaultInstruction);
            return SwiftUtil.limitInputString(65, sb.toString());
        } else {
            for(InstructionToBank instruction : instructionsToBank){
                sb.append("+").append(instruction.getInstruction()).append(SPACE);
            }
            sb.append(defaultInstruction);
            return SwiftUtil.limitInputString(65, sb.toString());
        }
    }
    
    
    public static String formatNarrative(int limit,String narrative){
    	return SwiftUtil.limitInputString(limit,narrative);
    }

    public static String formatSwiftCharges(Set<SwiftCharge> charges){
    	if(charges == null){
    		return EMPTY;
    	}
    	
        StringBuilder sb = new StringBuilder();
        for(SwiftCharge charge : charges){
            sb.append("/").append(charge.getCode())
                    .append(SwiftUtil.formatAmount(charge.getCurrency().toString(),charge.getAmount().toString()));
        }
        return SwiftUtil.formatMultiLine(sb.toString(),"/",35);
    }

    public static String formatSwiftDate(Date date) {
        if(date != null){
    	    return new java.text.SimpleDateFormat("yyMMdd").format(date);
        }
        return EMPTY;
    }
    
    public static String formatCurrencyAmount(Currency currency, BigDecimal amount){
    	if(currency == null || amount == null) {
    		return EMPTY;
    	}
    	
    	if(amount.toString().contains(".")){
    		return currency.toString() + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString().replace(".", ",");
    	}
    	return currency.toString() + amount.toString() + ",";
    
    }

    @Deprecated
    public static String formatCurrencyAmountDate(Currency currency, BigDecimal amount,Date date){
    	if(currency == null || amount == null || date == null){
    		return EMPTY;
    	}

        String output = new SimpleDateFormat("yyMMdd").format(date) + currency.toString();
    	if(amount.toString().contains(".")){
    	    return output + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString().replace(".", ",");
    	}
    	return output + amount.toString() + ",";
    }

    public static String formatCurrencyAmountDate(String currency, String amount,String date) throws ParseException {
        if(currency == null || amount == null || date == null){
            return EMPTY;
        }
        Currency convertedCurrency = Currency.getInstance(currency);
        BigDecimal convertedAmount = new BigDecimal(amount);
        Date convertedDate = DateUtil.convertToDate(date,"MM/dd/yyyy");
        return formatCurrencyAmountDate(convertedCurrency,convertedAmount,convertedDate);
    }
    
    public static String formatExpiryDatePlace(Date date, String place) {
    	if(date == null || place == null){
    		return EMPTY;
    	}
    	
    	return new java.text.SimpleDateFormat("yyMMdd").format(date) + " IN " + place;
    	
    }
    
    public static String forPercentageCreditAmountTolerance(Integer positiveToleranceLimit, Integer negativeToleranceLimit) {
    	if(positiveToleranceLimit == null || negativeToleranceLimit == null){
    		return EMPTY;
    	}
    	
    	return positiveToleranceLimit.toString() + "/" + negativeToleranceLimit.toString();
    }
    
    public static String formatAmount(Currency currency,BigDecimal amount){
    	if(currency == null || amount == null){
    		return EMPTY;
    	}
    	
        return currency.toString() + formatToSwift(amount);
    }

    private static String formatToSwift(BigDecimal amount){
    	if(amount == null){
    		return EMPTY;
    	}
    	
        String parsed = amount.toString();
        return parsed.contains(".") ? parsed.replace(".",",") : parsed + ",";
    }
    
    public static String formatIntegerToString(Integer num) {
        if(num == null){
            return EMPTY;
        }
    	return num.toString();
    }
}
