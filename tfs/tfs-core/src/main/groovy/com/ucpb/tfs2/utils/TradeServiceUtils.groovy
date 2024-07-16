package com.ucpb.tfs2.utils

import java.util.Arrays;
import java.util.List;
import java.util.regex.*;

import com.ucpb.tfs.domain.service.TradeService

class TradeServiceUtils {
	
	public static TradeService getSplittedValues(TradeService tradeService, Integer sequenceNumber){
		TradeService ts = tradeService.createCopy(tradeService);
		ts.details.put("sequenceOrder", (1+sequenceNumber).toString() +"/"+ getSequenceNumber(tradeService).toString())
		if(ts.details.get("serviceType").toString().equalsIgnoreCase("OPENING")){
			if(ts.details.get("priceTerm").toString().equalsIgnoreCase("OTH")){
				ts.details.put("generalDescriptionOfGoods", ts.details.get("generalDescriptionOfGoods").toString() +
					"\r\n+" + (ts.details?.get('priceTermNarrative') != null && ts.details?.get('priceTermNarrative').length() > 0 ? ts.details?.get('priceTermNarrative') : ts.details?.get('otherPriceTerm')));
			}else {
				ts.details.put("generalDescriptionOfGoods", ts.details.get("generalDescriptionOfGoods").toString() +
					"\r\n+" + ts.details?.get('priceTerm'));
			}
			ts.details.put("generalDescriptionOfGoods", getNext100Lines(ts.details.get("generalDescriptionOfGoods")?:'', sequenceNumber));
			ts.details.put("specialPaymentConditionsForBeneficiary", getNext100Lines(ts.details.get("specialPaymentConditionsForBeneficiary")?:'', sequenceNumber));
			ts.details.put("specialPaymentConditionsForReceivingBank", getNext100Lines(ts.details.get("specialPaymentConditionsForReceivingBank")?:'', sequenceNumber));
			ts.details.put("requiredDocument", getNext100Lines(formatMultiLine(ts.getRequiredDocument().collect { r -> r.description?.toUpperCase()})?:'', sequenceNumber));
			ts.details.put("additionalCondition", getNext100Lines(formatMultiLine(ts.getAdditionalCondition().collect { r -> r.condition?.toUpperCase()})?:'', sequenceNumber));								
			
		}else if(ts.details.get("serviceType").toString().equalsIgnoreCase("AMENDMENT")){
			ts.details.put("generalDescriptionOfGoodsTo", getNext100Lines(ts.details.get("generalDescriptionOfGoodsTo")?:'', sequenceNumber));
			ts.details.put("specialPaymentConditionsForBeneficiaryTo", getNext100Lines(ts.details.get("specialPaymentConditionsForBeneficiaryTo")?:'', sequenceNumber));
			ts.details.put("specialPaymentConditionsForReceivingBankTo", getNext100Lines(ts.details.get("specialPaymentConditionsForReceivingBankTo")?:'', sequenceNumber));
			ts.details.put("mtRequiredDocuments", getNext100Lines(formatMultiLineAmendment(ts.details.get("mtDocuments"))?:'', sequenceNumber));
			ts.details.put("mtAdditionalConditions", getNext100Lines(formatMultiLineAmendment(ts.details.get("mtConditions"))?:'', sequenceNumber));
		}
		return ts;
	}
	public static Integer getSequenceNumber(TradeService tradeService){
		int sequenceNumber, longest;
		
		if(tradeService.details.get("serviceType").toString().equalsIgnoreCase("OPENING")){
			String[] DescLines = tradeService.details.get("generalDescriptionOfGoods")?.split("\r\n")?: [];
			String[] SpBenifLines = tradeService.details.get("specialPaymentConditionsForBeneficiary")?.split("\r\n")?: [];
			String[] SpBankLines = tradeService.details.get("specialPaymentConditionsForReceivingBank")?.split("\r\n")?: [];
			String[] additionalConditionLines = formatMultiLine(tradeService.getAdditionalCondition().collect { r -> r.condition?.toUpperCase()})?.split("\r\n")?: [];
			String[] requiredDocumentLines = formatMultiLine(tradeService.getRequiredDocument().collect { r -> r.description?.toUpperCase()})?.split("\r\n")?: [];

			Integer[] fieldList = [DescLines.length, SpBenifLines.length, SpBankLines.length, additionalConditionLines.length, requiredDocumentLines.length];
			fieldList.sort()
			longest = fieldList[4];
			
			sequenceNumber = longest / 100;
        	if(longest % 100 > 0) { sequenceNumber =  sequenceNumber + 1; }
			
			
		}else if(tradeService.details.get("serviceType").toString().equalsIgnoreCase("AMENDMENT")){
			String[] DescLines = tradeService.details.get("generalDescriptionOfGoodsTo")?.split("\r\n")?: [];
			String[] SpBenifLines = tradeService.details.get("specialPaymentConditionsForBeneficiaryTo")?.split("\r\n")?: [];
			String[] SpBankLines = tradeService.details.get("specialPaymentConditionsForReceivingBankTo")?.split("\r\n")?: [];
			String[] mtDocuments = formatMultiLineAmendment(tradeService.details.get("mtDocuments"))?.split("\r\n")?: [];
			String[] mtConditions = formatMultiLineAmendment(tradeService.details.get("mtConditions"))?.split("\r\n")?: [];
			
			Integer[] fieldList = [DescLines.length, SpBenifLines.length, SpBankLines.length, mtDocuments.length, mtConditions.length];
			fieldList.sort()
			longest = fieldList[4];
			
			sequenceNumber = longest / 100;
        	if(longest % 100 > 0) { sequenceNumber =  sequenceNumber + 1; }
		}
		return sequenceNumber
	}
	private static String getNext100Lines(String field, Integer sequenceNumber){
		String[] lines = field.split("\r\n");
		
		int totalLines = (lines.length / 100) + ((lines.length % 100 > 0) ? 1 : 0);
		
		if(sequenceNumber > (totalLines - 1)){
			return ""
		} else if(lines.length > 100) {
			StringBuilder finalField = new StringBuilder();
			for(int p = (sequenceNumber * 100); p < ((sequenceNumber + 1) * 100) && p < lines.length; p++){
				finalField.append(lines[p]+"\r\n");
			}
			return finalField
		}
		return field
	}
    public static String formatMultiLine(List<String> sourceString){
		if(sourceString != null){
			if(!sourceString.isEmpty()){
				StringBuilder sb = new StringBuilder();
				for(String perList : sourceString){
					if(perList.contains('<br>') || perList.contains('/n') || perList.contains('<br/>') || perList.contains('<BR>') || perList.contains('/N') || perList.contains('<BR/>')|| perList.contains('\r')|| perList.contains('\n')|| perList.contains('\r\n')|| perList.contains('\\r')|| perList.contains('\\n')|| perList.contains('\\r\\n')) {
						sb.append(("+"+perList).split("(/N)|(<BR>)|(<BR/>)|(/n)|(<br>)|(<br/>)|(\\r)|(\\n)|(\\r\\n)").join("\r\n")).append("\r\n")
					} else if(!perList.contains("\r\n")){
						sb.append(("+"+perList).split("(?<=\\G.{65})").join("\r\n")).append("\r\n")
					} else {
						sb.append("+").append(perList).append("\r\n");
					}
				}
				return sb.toString();
			}
			return "";
		}
		return "";
    }
    public static String formatMultiLineAmendment(String sourceString){
		if(sourceString != null){
			if(!sourceString.isEmpty()){
				StringBuilder sb = new StringBuilder();
				sourceString = sourceString.replace("|", "\r\n")
				List<String> lines = Arrays.asList(sourceString.split("\r\n"));
				for(String perList : lines){
					for (int i = 0; i < perList.length(); i++) {
						if (i > 0 && (i % 65 == 0)) {
							sb.append("\r\n");
						}
						sb.append(perList.charAt(i));
					}
					sb.append("\r\n");
				}
				return sb.toString();
			}
			return "";
		}
		return "";
    }
}
