package com.ucpb.tfs.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.ucpb.tfs.swift.message.writer.DefaultSwiftMessageWriter;

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Description: RM 6841 - SWIFT MT 799 FORMAT ERROR
	[Date Revised:] 05/28/2018
	Program [Revision] Details: limitInputStringMT799 Adding space in the beginning of the line. If line begins with special char.
	PROJECT: tfs-swift
	MEMBER TYPE  : Java
	Project Name: SwiftUtil
	
 * 	(revision)
 	Modified by : Rafael Ski Poblete
	Date modified: 07/26/2018
	Description : Added formatChargeNarrative function to set limit and delimiter.
	
	Date modified: 08/08/2018
	Description : Added formatSenderToReceiver730and742 function that set limit and delimiter to be use by MT730 and MT742 modules.
	
	Date modified: 08/28/2018 
	Description : Added formatConditions function to set limit and delimiter.
				  Added checkCountryName function to set limit and delimiter and prioritize otherplace field as country name.
 	
 	Date modified: 09/11/2018 
	Description : Added appendSwiftStartingPrefix, formatAmountWithNarrative, formatNarrative function to be use in MT752.

 * 	(revision)
 	Modified by : Cedrick C. Nungay
	Date modified: 08/31/2018
	Description : Added formatRequiredDocuments function.
 */
public class SwiftUtil {

    private static final String EMPTY = "";
    private static final String SWIFT_DATE_FORMAT = "yyMMdd";
    private static final String TFS_DATE_FORMAT = "MM/dd/yyyy";

    static public final String WITH_DELIMITER = "(?=\\+)";

    public static String formatTimeIndication(String code, String timeIndication){
        return appendSeparator(code) + (timeIndication != null ? timeIndication : EMPTY);

    }

    public static String formatPartyIdentifier(String clearingCode, String identifier, String identifierCode){
    	SimpleStringBuilder simpleStringBuilder = new SimpleStringBuilder();
        if(StringUtils.hasText(clearingCode)){
            simpleStringBuilder.append("//").append(clearingCode);
        }
        simpleStringBuilder.append(identifier);
        if(StringUtils.hasText(identifierCode)){
            if(simpleStringBuilder.hasText()){
                simpleStringBuilder.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
            simpleStringBuilder.append(identifierCode);
        }
        return simpleStringBuilder.toString();
    }

    public static String formatPartyIdentifierAddress(String clearingCode, String identifier,String address){
    	SimpleStringBuilder sb = new SimpleStringBuilder();
        sb.append(formatPartyIdentifier(clearingCode,identifier,null));
        if(sb.hasText()){
            sb.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        sb.append(limitInputString(35,address));
        return sb.toString();
    }

    public static String formatRate(String rate){
        if(!StringUtils.hasText(rate)){
            return EMPTY;
        }
        BigDecimal rateNumber = new BigDecimal(rate.replaceAll(",",""));
        return rateNumber.toString().replaceAll("\\.",",");
    }

    public static String formatSwiftLocation(String partyIdentifier, String location){
        return formatSwiftLocation(partyIdentifier,null,location);
    }

    public static String formatSwiftLocation(String partyIdentifier, String name, String location){
    	SimpleStringBuilder stringBuilder = new SimpleStringBuilder();
        if(StringUtils.hasText(partyIdentifier)){
            if(!partyIdentifier.startsWith("/")){
                stringBuilder.append('/').append(partyIdentifier).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            } else {
                stringBuilder.append(partyIdentifier).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
//            stringBuilder.append('/').append(partyIdentifier).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);

        }
        stringBuilder.appendAndRestrictWithoutInitialize(35,name,location);
//        stringBuilder.append(limitInputString(35,name)).
//        append(DefaultSwiftMessageWriter.SWIFT_NEWLINE).
//        append(limitInputString(35,location));
        return stringBuilder.toString();
    }
    
    public static String formatTenorOfDraft(String usancePeriod, String narrative){
    	if(narrative == null){
    		return EMPTY;
    	}
    	SimpleStringBuilder stringBuilder = new SimpleStringBuilder();
    	
    	if(StringUtils.hasText(usancePeriod)){
    		stringBuilder.append(usancePeriod).append(" DAYS ").append(narrative);    		
    	}else{
    		stringBuilder.append(narrative);
    	}
        return limitInputString(35, stringBuilder.toString());
    }

    public static String appendSwiftStartingPrefix(String input){
    	if(input == null || !StringUtils.hasText(input)){
    		return "";
    	}
    	return DefaultSwiftMessageWriter.SWIFT_START_PREFIX + input; 
    }

    public static String appendSwiftStartingPrefix(String input, String location){
    	if(input == null || !StringUtils.hasText(input)){
    		return "";
    	}
    	return DefaultSwiftMessageWriter.SWIFT_START_PREFIX + input + location; 
    }
    
    public static String formatDateString(String targetFormat, String dateStringFormat,String dateString) throws ParseException {
        if(targetFormat == null || dateStringFormat == null || dateString == null || !StringUtils.hasText(dateString)){
            return EMPTY;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateStringFormat);
        return formatToString(targetFormat,simpleDateFormat.parse(dateString));
    }

    public static String formatToString(String format, Date date){
    	if((format == null || "" == format) || date == null){
    		return EMPTY;
    	}
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String formatAmount(String currency,String amount){
        if((currency != null && amount != null) && 
        		(StringUtils.hasText(currency) && StringUtils.hasText(amount))){
        	//JPY and CHF only has whole numbers
        	//if(currency.equalsIgnoreCase("JPY") || currency.equalsIgnoreCase("CHF")){
        	//	return currency + formatAmount(amount,0);
        	//}else{
        	//	return currency + formatAmount(amount,2);        		
        	//}
			//Regardless of currency, show the decimal part of the amount.
			return currency + formatAmount(amount,2);
        }
        return EMPTY;
    }
    public static String formatAmountWithNarrative(String currency,String amount,String narrative){
    	SimpleStringBuilder sb = new SimpleStringBuilder();
        if((currency != null && amount != null) && 
        		(StringUtils.hasText(currency) && StringUtils.hasText(amount))){
        	sb.append(formatAmount(currency,amount));
        	sb.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        if(narrative != null && StringUtils.hasText(narrative)) {
        	sb.append(limitInputString(35, narrative));
        }
        if(sb != null && StringUtils.hasText(sb.toString())){
        	return sb.toString();
        }
        return EMPTY;
    }

    public static String formatDiscrepancies(String expiredLcFlag,String overdrawnFlag, String overdrawnAmount, 
    		String descriptionOfGoodsFlag, String documentsNotPresentedFlag,String othersFlag, String otherDetails,
    		String descriptionOfGoodDetails, String documentsNotPresentedDetails ){
    	SimpleStringBuilder sb = new SimpleStringBuilder();
        if("on".equals(expiredLcFlag)){
            sb.append("Expired LC");
        }

        
//        (details.get('expiredLcSwitchDisplay') == 'on' ? 'LC has expired' : '') +
//	    (details.get('overdrawnForAmountSwitchDisplay') == 'on' ? 'LC amount is overdrawn for ' + details.get('overdrawnForAmount') : '') +
//	    (details.get('descriptionOfGoodsNotPerLcSwitchDisplay') == 'on' ? 'Description of goods is not per LC' : '') +
//	    (details.get('documentsNotPresentedSwitchDisplay') == 'on' ? 'Documents were not presented' : '') +
//	    (details.get('othersSwitchDisplay') == 'on' ? details.get('others')        
        
        if("on".equals(overdrawnFlag) && StringUtils.hasText(overdrawnAmount)){
        	if(sb.equals("")) {
        		sb.append("LC is overdrawn for " + formatAmount(overdrawnAmount,2));
        	} else {
        		sb.appendWithNewLine("LC is overdrawn for " + formatAmount(overdrawnAmount,2));
        	}
        }

        if("on".equals(descriptionOfGoodsFlag) && StringUtils.hasText(descriptionOfGoodDetails)){
        	if(sb.equals("")) {
        		sb.append("Description of goods not as per LC:");
        	} else {
        		sb.appendWithNewLine("Description of goods not as per LC:");
        	}
        	sb.appendWithNewLine(descriptionOfGoodDetails);
        }

        if("on".equals(documentsNotPresentedFlag) && StringUtils.hasText(documentsNotPresentedDetails)){
        	if(sb.equals("")) {
        		sb.append("Documents not Presented:");
        	} else {
        		sb.appendWithNewLine("Documents not Presented:");
        	}
            sb.appendWithNewLine(documentsNotPresentedDetails);
        }

        if("on".equals(othersFlag) && StringUtils.hasText(otherDetails)){
        	if(sb.equals("")) {
        		sb.append("Others:");
        	} else {
        		sb.appendWithNewLine("Others:");
        	}
        	sb.appendWithNewLine(otherDetails);
        }

        return limitInputString(50, sb.toString());
    }
    
    // For Discrepancy
//    public static String replaceAllCarriage(String value) {
//    	return value.replaceAll("\r\n", "\r\n\t");
//    }

    public static String formatInstructionCode(String code, String additionalInformation){
    	SimpleStringBuilder simpleStringBuilder = new SimpleStringBuilder();
        simpleStringBuilder.append(code);
        if(StringUtils.hasText(additionalInformation)){
            simpleStringBuilder.append('/').append(additionalInformation);
        }
        return simpleStringBuilder.toString();
    }

    public static String formatInstructionsToBank(Map<String,Object> instructionsToBank){
        if(instructionsToBank == null){
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,Object> instruction : instructionsToBank.entrySet()){
//            sb.append("+").append(instruction.getInstruction());
        }
        return SwiftUtil.formatMultiLine(sb.toString(),65);
    }

    public static String formatAmount(BigDecimal amount){
    	if(amount == null){
    		return EMPTY;
    	}
        return amount.toString().replaceAll(".",",");
    }

    public static String formatSenderToReceiver(String code1,String narrative1){
    	System.out.println("code1: " + code1); // test by robin
    	System.out.println("narrative1: " + narrative1); //test by robin
    	if(narrative1 == null || !StringUtils.hasText(narrative1)){
    		System.out.println("if narrative1 null asd"); //test by robin
    		//return EMPTY;	//4116 comment-out by robin.
    	}
        String remittanceInfo = EMPTY;
        String transformedString = narrative1.replaceAll("//", EMPTY);
        System.out.println("transformedString: " + transformedString); //test by robin
        boolean hasCode = Pattern.compile("^(?!/).+/",Pattern.DOTALL).matcher(transformedString).find();
        if(code1 != null && StringUtils.hasText(code1)){
        	System.out.println("if code 1 != null"); //test by robin
        	transformedString = transformedString.replaceAll("/*.+/", EMPTY);
            remittanceInfo = "/" + code1 + "/";
            System.out.println("remittanceInfo: " + remittanceInfo); //test by robin
        }else{
        	System.out.println("else"); //test by robin
        	if(transformedString != null && StringUtils.hasText(transformedString) 
        			&& !transformedString.startsWith("/") & !hasCode){
        		remittanceInfo = ""; // comment by robin for 4116: remove double slash if my has no code and with narrative
        		System.out.println("if transformedString != null"); //test by robin
        	}else if(hasCode){
        		System.out.println("else if (bool)hasCode: " + hasCode);
        		remittanceInfo = "/";        	        		        		
        	}
        }

        if(StringUtils.hasText(transformedString)){
            remittanceInfo +=transformedString;
            System.out.println("if stringutilshastext, remittanceInfo += transformedString: " + remittanceInfo); //test by robin
        }
        System.out.println("asd remittanceinfo: " + remittanceInfo); // test by robin
        System.out.println("return: " + formatToLimitWithDelimiter(33,"//",false,remittanceInfo)); //test by robin
        return formatToLimitWithDelimiter(33,"//",false,remittanceInfo);
    }
    
    public static String formatSenderToReceiver(String code1, String narrative1, String mtFlag){
        String remittanceInfo = EMPTY;
        String transformedString = narrative1.replaceAll("//", EMPTY);
        boolean hasCode = Pattern.compile("^(?!/).+/",Pattern.DOTALL).matcher(transformedString).find();
        if( code1 != null && StringUtils.hasText(code1) ){
        	transformedString = transformedString.replaceAll("/*.+/", EMPTY);
            remittanceInfo = "/" + code1 + "/";
        } else {
        	if( transformedString != null && StringUtils.hasText(transformedString) 
        			&& !transformedString.startsWith("/") & !hasCode ){
        		remittanceInfo = ""; 
        	} else if ( hasCode ){
        		remittanceInfo = "/";        	        		        		
        	}
        }
        if( StringUtils.hasText(transformedString) ){
            remittanceInfo +=transformedString;
        }
        if( mtFlag.equalsIgnoreCase("mt734") ){
        	return remittanceInfo;
        } else {
        	return formatToLimitWithDelimiter(33,"//",false,remittanceInfo);
        }
    }

    public static String formatChargeNarrative(String narrative1){
        	return formatToLimitWithDelimiter(35,"",true,narrative1);
    }
    public static String formatConditions(String condition){
    	return formatToLimitWithDelimiter(100,"",true,condition);
    }
    public static String formatNarrative(String narrative1){
    	return formatToLimitWithDelimiter(35,"",true,narrative1);
    }	
    public static String formatNarrativeBy50(String narrative1){
    	return formatToLimitWithDelimiter(50,"",true,narrative1);
    }	
    
    public static String formatFileIdentification(String fiCode, String fiDesc){
    	if(fiCode != null && !fiCode.equalsIgnoreCase("") && !StringUtils.hasText(fiDesc)){ 
    		return fiCode;
    	} else if (fiDesc != null && !fiDesc.equalsIgnoreCase("") && !StringUtils.hasText(fiCode)){ 
    		return fiDesc;
    	} else if (fiDesc != null && !fiDesc.equalsIgnoreCase("") && StringUtils.hasText(fiCode)){
    		return fiCode + '/' + fiDesc; 
    	} else {
    		return "";
    	}
    }
    
    public static String checkCountryName(String countryName, String otherPlaceOfExpiry){
        String place = EMPTY;
    	if(StringUtils.hasText(otherPlaceOfExpiry)) {
    		place = otherPlaceOfExpiry;
    	} else {
    		place = countryName;
    	}
        	return place;
    }
    
    public static String formatSenderToReceiver730and742(String code1, String narrative1){
    	String remittanceInfo = EMPTY;
        String newLineFormat = EMPTY;
        Integer lineNumber = 0;
        if( code1 != null && !code1.equalsIgnoreCase("") && !StringUtils.hasText(narrative1)) {
            remittanceInfo = "/" + code1 + "/";
            newLineFormat = "//";
            lineNumber = 33;
        } else if (code1 != null && !code1.equalsIgnoreCase("") && StringUtils.hasText(narrative1)){
            newLineFormat = "//";
            lineNumber = 33;
            remittanceInfo = narrative1;
        } else {
            newLineFormat = "";
            lineNumber = 35;
            remittanceInfo = narrative1;
        }
    	return formatToLimitWithDelimiter(lineNumber,newLineFormat,true,remittanceInfo);
    }
    
    public static String formatRemittanceInfo(String code1,String narrative1){
       	if(narrative1 == null || !StringUtils.hasText(narrative1)){
    		return EMPTY;
    	}
        String remittanceInfo = EMPTY;
        String transformedString = narrative1.replaceAll("//", EMPTY);
        boolean hasCode = Pattern.compile("^(?!/).+/",Pattern.DOTALL).matcher(transformedString).find();
        if(code1 != null && StringUtils.hasText(code1)){
            remittanceInfo = "/" + code1 + "/";
        }else{
        	if(transformedString != null && StringUtils.hasText(transformedString) 
        			&& !transformedString.startsWith("/") & !hasCode){
        		remittanceInfo = "//";        	        		
        	}else if(hasCode){
        		remittanceInfo = "/";        	        		        		
        	}
        }

        if(StringUtils.hasText(transformedString)){
            remittanceInfo +=transformedString;
        }
        return formatToLimitWithDelimiter(33,"//",true,remittanceInfo);
    }
    
    public static String formatDisposalOfDocuments(String code1,String narrative1){
    	if(narrative1 == null || !StringUtils.hasText(code1)){
    		return EMPTY;
    	}
        String remittanceInfo = EMPTY;
        String transformedString = narrative1.replaceAll("//", EMPTY);
        boolean hasCode = Pattern.compile("^(?!/).+/",Pattern.DOTALL).matcher(transformedString).find();
        if(code1 != null && StringUtils.hasText(code1)){
        	transformedString = transformedString.replaceAll("/*.+/", EMPTY);
            remittanceInfo = "/" + code1 + "/";
        }else{
        	if(transformedString != null && StringUtils.hasText(transformedString) 
        			&& !transformedString.startsWith("/") & !hasCode){
        		remittanceInfo = "//";        	        		
        	}else if(hasCode){
        		remittanceInfo = "/";        	        		        		
        	}
        }

        if(StringUtils.hasText(transformedString)){
            remittanceInfo +=transformedString;
        }
        return formatToLimitWithDelimiter(35,"//",true,remittanceInfo);
    }
    
    public static String getAmountIncrease(String currency,String originalAmount, String newAmount){
    	if(currency == null || originalAmount == null || originalAmount.isEmpty() ||
    			newAmount == null || newAmount.isEmpty()){
    		return EMPTY;
    	}
        BigDecimal difference = getDifference(originalAmount,newAmount);
        if(BigDecimal.ZERO.compareTo(difference) > 0){
            return formatAmount(currency,difference.toString());
        }
        return EMPTY;
    }

    public static String getAmountDecrease(String currency, String originalAmount, String newAmount){
    	if(currency == null || originalAmount == null || originalAmount.isEmpty() ||
    			newAmount == null || newAmount.isEmpty()){
    		return EMPTY;
    	}
        BigDecimal difference = getDifference(originalAmount,newAmount);
        if(BigDecimal.ZERO.compareTo(difference) < 0){
            return formatAmount(currency,difference.toString());
        }
        return EMPTY;
    }

    public static String getAmountTolerance(String positiveTolerance, String negativeTolerance){
        if(!StringUtils.hasText(positiveTolerance) || !StringUtils.hasText(negativeTolerance)){
            return EMPTY;
        }
        return new BigDecimal(positiveTolerance).intValue() + "/" + new BigDecimal(negativeTolerance).intValue();
    }

    private static BigDecimal getDifference(String originalAmount, String finalAmount){
    	originalAmount=originalAmount.replaceAll(",", "");
    	finalAmount=finalAmount.replaceAll(",","");
        return toBigDecimal(originalAmount).subtract(toBigDecimal(finalAmount));
    }

    public static String formatDateAmount(String dateString, String currency,String amount) throws ParseException {
    	if(amount == null || currency == null || dateString == null){
    		return EMPTY;
    	}
    	return formatToSwiftDate(dateString) + formatAmount(currency,amount);
    }

    public static String formatDetailsOfAmount(String code, String currency, String amount, String narrative){
        SimpleStringBuilder output = new SimpleStringBuilder();

        appendSwiftCode(code,output);

        output.append(formatAmount(currency,amount));
        if(StringUtils.hasText(narrative)){
            output.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            List<String> split = restrictPreserveSpace(narrative,33);
            for(String row : split){
                output.append("//").append(row).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
        }
        return trimTrailingWhitespace(output);
    }

    public static String chooseNotEmpty(String... values){
        for(String value : values){
            if(StringUtils.hasText(value)){
                return value;
            }
        }
        return "";
    }

    public static String formatToSwiftDate(String dateString) throws ParseException {
        if(!StringUtils.hasText(dateString)){
            return EMPTY;
        }
        return formatDateString(SWIFT_DATE_FORMAT, TFS_DATE_FORMAT,dateString);
    }

    public static String formatToSwiftDate(Date date) throws ParseException {
    	if(date == null) {
    		return EMPTY;
    	}
        return formatToString(SWIFT_DATE_FORMAT, date);
    }

    public static String[] split(String sourceString, int limit){
        String[] parts = new String[2];
        int splitLimit = getSplitLimit(sourceString,limit);
        parts[0] = StringUtils.trimWhitespace(sourceString.substring(0,splitLimit));
        parts[1] = StringUtils.trimWhitespace(sourceString.substring(splitLimit,sourceString.length()));
        return parts;
    }

    public static String limitInputString(int limit, String inputString){
    	if(inputString == null || !StringUtils.hasText(inputString)){
    		return EMPTY;
    	}
        SimpleStringBuilder sb = new SimpleStringBuilder();
        String formattedString = inputString;
        if(formattedString.contains("\\r\\n\\r\\n")){
            formattedString = formattedString.replaceAll("\\r\\n\\r\\n", DefaultSwiftMessageWriter.SWIFT_NEWLINE);         	
        }       
        for(String line : restrictPreserveSpace(formattedString,limit)){
                sb.append(line).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
	    return trimTrailingWhitespace(sb);
    }
    
    public static String limitInputStringForField78(int limit, String inputString){
        
        SimpleStringBuilder sb = new SimpleStringBuilder();
        
        String defaultInstruction = "+ALL OTHER TERMS AND CONDITIONS REMAIN UNCHANGED.";
        
        if(inputString == null || !StringUtils.hasText(inputString)){
            sb.append(defaultInstruction);
            return trimTrailingWhitespace(sb);
        } else {
            String formattedString = inputString;
            if(formattedString.contains("\\r\\n\\r\\n")){
                formattedString = formattedString.replaceAll("\\r\\n\\r\\n", DefaultSwiftMessageWriter.SWIFT_NEWLINE);          
            }       
            for(String line : restrictPreserveSpace(formattedString,limit)){
                sb.append(line).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
            sb.append(defaultInstruction);
            return trimTrailingWhitespace(sb);
        }
    }

    public static String limitInputStringMT799(int limit, String inputString){
    	if(inputString == null || !StringUtils.hasText(inputString)){
    		return EMPTY;
    	}
        SimpleStringBuilder sb = new SimpleStringBuilder();
        String formattedString = inputString;
        ArrayList<String> line = new ArrayList<String>();
        
        for(String line1 : restrictPreserveSpace(formattedString,limit)){
          	if (!line1.startsWith(" ")) {
        		if (!line1.matches("^[a-zA-Z].*$")) {
        			line.add(" " + line1);
        		}else{
        			line.add(line1);
        		}
        	}else{
        		line.add(line1);
        	}
        }
        for(String lines : line){
        	for(String line3 : restrictPreserveSpace(lines,limit)){
            	sb.append(line3).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }        
        }        
        return trimTrailingWhitespace(sb);
    }

    //This method has been deprecated because it was discovered that Spel Evaluations on this method where
    // the input string contains commas, the Spel engine will evaluate the input string as a String array/Varargs input
    @Deprecated
    public static String formatToLimit(int limit, String... inputStrings){
        List<String> result = new ArrayList<String>();
        for(String inputString : inputStrings){
            String cleanedString = clean(inputString);
            result.addAll(restrict(deleteDuplicateWhitespaces(cleanedString),limit));
        }
        SimpleStringBuilder sb = new SimpleStringBuilder();
        for(String line : result){
            sb.append(line).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        
        return trimTrailingWhitespace(sb);
    }

    public static String formatToLimitWithDelimiter(int limit,String delimiter,boolean preserveSpace, String... inputStrings){
        List<String> result = new ArrayList<String>();
        for(String inputString : inputStrings){
        	if(preserveSpace){
        		result.addAll(restrictPreserveSpace(inputString,limit));        		
        	}else{
        		String cleanedString = clean(inputString);
        		result.addAll(restrict(cleanedString,limit));
        	}
        }
        StringBuilder sb = new StringBuilder();
        String replacedString = EMPTY;
        for(int ctr = 0; ctr < result.size(); ctr++){
        	replacedString = ctr == 0 ? result.get(ctr) : result.get(ctr).replaceAll(delimiter, EMPTY);
            if(ctr != 0 ){
                sb.append(delimiter);
            }
            sb.append(replacedString).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        return trimTrailingWhitespace(sb);
    }

    public static List<String> restrict(String sourceString, int limit){
        List<String> result = new ArrayList<String>();
        if(sourceString == null){
            return result;
        }
        StringBuilder builder; 
      
        if((sourceString.contains("<BR/>") || sourceString.contains("<br/>")) || 
        		(sourceString.contains("<BR>") || sourceString.contains("<br>"))){
        	builder = new StringBuilder(sourceString.replaceAll("(<(BR|br)/?>\\.)|(<(BR|br)/?>)", DefaultSwiftMessageWriter.SWIFT_NEWLINE));
        }else{
        	builder = new StringBuilder(sourceString.replaceAll("\\s"," "));
        }
        
        while(builder.length() > limit){
            int splitLimit = getSplitLimit(builder,limit);

            //if string can no longer be split (e.g. input string is really long and it has no whitespaces on or before
            // the limit specified)
            if(splitLimit == 0){
                result.add(builder.toString());
                break;
            }else{
                result.add(builder.substring(0,splitLimit));
            }
//            sourceString = sourceString.substring(splitLimit,sourceString.length());
            builder.delete(0,splitLimit);
        }

        if(builder.length() <= limit){
            result.add(builder.toString());
            return result;
        }
        return result;
    }
    
    public static List<String> restrictPreserveSpace(String sourceString, int limit){
        List<String> result = new ArrayList<String>();
        if(sourceString == null){
            return result;
        }
      
        if((sourceString.contains("<BR/>") || sourceString.contains("<br/>")) || 
        		(sourceString.contains("<BR>") || sourceString.contains("<br>"))){
        	sourceString = sourceString.replaceAll("(<(BR|br)/?>\\.)|(<(BR|br)/?>)", DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        
        if(sourceString.contains("\\r\\n\\r\\n")){
        	sourceString = sourceString.replaceAll("\\r\\n\\r\\n", DefaultSwiftMessageWriter.SWIFT_NEWLINE);         	
        }       
        
        List<String> splittedStrings = splitEachNewLine(sourceString);
        StringBuilder builder=new StringBuilder(); 
        
        for(String s: splittedStrings){
        	if(s != null && !s.isEmpty()){
        		builder=new StringBuilder(s);
        		while(builder.length() > limit){
        			int splitLimit = getSplitLimit(builder,limit);
        			
        			if(splitLimit == 0){
        				result.add(builder.toString());
        				break;
        			}else{
        				result.add(builder.substring(0,splitLimit));
        			}
        			builder.delete(0,splitLimit);
        		}
        		
        		if(builder.length() <= limit){
        			result.add(builder.toString());
        		}
        	}
        }
        
        return result;
    }

    public static String formatMultiLine(String sourceString, String delimiter, int limit){
        if(!StringUtils.hasText(sourceString)){
            return EMPTY;
        }
        String[] split = sourceString.split("(?=\\" + delimiter + ")");
        List<String> result = new ArrayList<String>();
        for(int ctr = 0; ctr < split.length; ctr++){
            if(StringUtils.hasText(split[ctr])){
                result.addAll(restrictPreserveSpace(split[ctr],limit));
            }
        }
        StringBuilder sb = new StringBuilder();
        for(String parsedString : result){
            sb.append(parsedString).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        return removeWhiteLinesAndTrailingWhiteSpace(sb);
    }
    
//    public static String formatMultiLine(String sourceString, String delimiter, int limit){
//    	if(!StringUtils.hasText(sourceString)){
//    		return EMPTY;
//    	}
//    	String[] split = clean(sourceString).split("(?=\\" + delimiter + ")");
//    	List<String> result = new ArrayList<String>();
//    	for(int ctr = 0; ctr < split.length; ctr++){
//    		if(StringUtils.hasText(split[ctr])){
//    			result.addAll(restrict(split[ctr],limit));
//    		}
//    	}
//    	StringBuilder sb = new StringBuilder();
//    	for(String parsedString : result){
//    		sb.append(parsedString).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
//    	}
//    	return trimTrailingWhitespace(sb);
//    }

    public static String formatMultiLine(String sourceString, int limit){
        return formatMultiLine(sourceString,"+",limit);
    }

    public static int getSplitLimit(String sourceString, int limit){
        char[] source = sourceString.toCharArray();
        while(limit > 0 && !Character.isWhitespace(source[limit-1])){
            limit--;
        }
        return limit;
    }

    private static int getSplitLimit(StringBuilder sourceString, int limit){
        while(limit > 0 && !Character.isWhitespace(sourceString.charAt(limit-1))){
            limit--;
        }
        return limit;
    }

    public static String splitString(String sourceString, int limit){
        StringBuilder sb = new StringBuilder();
        while(sourceString.length() > limit){
            sb.append(substring(sourceString,0,limit));
            sourceString = sourceString.substring(limit,sourceString.length());
            sb.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        return sb.toString();
    }

    private static String substring(String source, int start, int end){
        int endIndex = source.length() > end ? end : source.length();
        return source.substring(start,endIndex);
    }


    public static String concatWithNewLine(String... input){
        StringBuilder sb = new StringBuilder();
        for(String string : input){
            if(StringUtils.hasText(string)){
                sb.append(string);
                sb.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
        }

        return trimTrailingWhitespace(sb);
    }

    private static String formatAmount(String amount,int decimalScale){
        String formattedAmount = amount.replaceAll(",",EMPTY);
        BigDecimal trueAmount = decimalScale == 0 ? new BigDecimal(formattedAmount).setScale(decimalScale,BigDecimal.ROUND_FLOOR) 
        		: new BigDecimal(formattedAmount).setScale(decimalScale,BigDecimal.ROUND_HALF_UP);
        if(BigDecimal.ZERO.compareTo(trueAmount) > 0){
        	trueAmount=trueAmount.abs();
        }
        if(trueAmount.scale() == 0){
        	return trueAmount.toString() + ",";
        }
        if(BigDecimal.ZERO.compareTo(trueAmount.remainder(BigDecimal.ONE)) < 0){
            return trueAmount.toString().replaceAll("\\.",",");
        }
        return trueAmount.toString().replaceAll("\\.00",",");
    }
    

    
    

    private static String getValue(String value){
        return value != null ? value : EMPTY;
    }

    private static BigDecimal toBigDecimal(String value){
        return new BigDecimal(value.replaceAll(",",EMPTY));
    }

    private static String appendSeparator(String input){
        if(StringUtils.hasText(input)){
            return "/" + input + "/";
        }
        return EMPTY;
    }

    private static String trimTrailingWhitespace(StringBuilder sb){
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String trimTrailingWhitespace(SimpleStringBuilder sb){
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String deleteDuplicateWhitespaces(String string){
        return deleteDuplicateWhitespaces(new SimpleStringBuilder(string));
    }

    private static String deleteDuplicateWhitespaces(SimpleStringBuilder sb){
        int ctr = sb.length() - 1;
        while(ctr >= 0){
            if(Character.isWhitespace(sb.charAt(ctr))){
                sb.setCharAt(ctr,' ');
                if((ctr -1 >= 0) && Character.isWhitespace(sb.charAt(ctr-1))){
                    sb.deleteCharAt(ctr -1);
                }
            }
            ctr--;
        }
        return sb.toString();
    }

    public static void replaceAllWhitespaces(StringBuilder sb){
        int ctr = 0;
        while(ctr < sb.length()){
            if(sb.charAt(ctr) == '\r'){
                sb.setCharAt(ctr,' ');
                if(ctr + 1 < sb.length() && sb.charAt(ctr+1) == '\n'){
                    sb.deleteCharAt(ctr+1);
                }
            }
            ctr++;
        }
    }

    private static void appendSwiftCode(String code,SimpleStringBuilder simpleStringBuilder){
        if(StringUtils.hasText(code)){
            simpleStringBuilder.append('/').append(code).append('/');
        }
    }

    private static String clean(String input){
        if(input == null){
            return  EMPTY;
        }
        return input.replaceAll("\\r"," ")
                .replaceAll("\\n"," ")
                .replaceAll("\\\\r"," ")
                .replaceAll("\\\\n"," ");
    }
    
    private static List<String> splitEachNewLine(String s){
    	String[] temp=s.split("\\n|\\r\\n|\\r|\\\\n|\\\\r");
    	if(temp != null){
    		return new ArrayList<String>(Arrays.asList(temp));    		    		
    	}else{
    		return null;
    	}
    }
    
    private static String removeWhiteLinesAndTrailingWhiteSpace(StringBuilder stringBuilder){    	
    	List<String> temp=splitEachNewLine(stringBuilder.toString());
    	StringBuilder result=new StringBuilder();
    	for(String s:temp){
    		if(StringUtils.hasText(s)){
    			result.append(s).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
    		}
    	}
        return trimTrailingWhitespace(result);
    }
    
    public static boolean startsWithDigit(String testString){
    	if(testString == null || !StringUtils.hasText(testString)){
    		return false;
    	}
    	
    	if(Character.isDigit(testString.charAt(0))){
    		return true;
    	}
    	 
    	return false;
    }

    public static String formatRequiredDocuments(String mtDocuments) {
        String[] d = mtDocuments.split("|");
        StringBuilder sb = new StringBuilder();
        for(String string : mtDocuments.split("\\|")){
            if(StringUtils.hasText(string)){
                sb.append(string);
                sb.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
        }

        return limitInputString(65, sb.toString());
    }
}